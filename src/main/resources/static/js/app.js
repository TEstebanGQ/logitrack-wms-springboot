/* ==========================================
   LogiTrack S.A. - Main Application Orchestrator
   ========================================== */

const App = {
    init() {
        console.log('LogiTrack S.A. Frontend Orquestado Inicializado...');
        this.bindEvents();
        this.initCapsLockDetectors();
        this.initCommandPalette();
        Router.init();
        this.updateUserInfo();
    },

    // Mobile Hamburger Sidebar Toggle
    toggleSidebar() {
        const sidebar = document.querySelector('.sidebar');
        const overlay = document.getElementById('sidebar-overlay');
        const btn = document.getElementById('btn-hamburger');
        if (!sidebar) return;
        const isActive = sidebar.classList.toggle('active');
        if (overlay) overlay.classList.toggle('active', isActive);
        if (btn) btn.classList.toggle('active', isActive);
    },

    closeSidebar() {
        const sidebar = document.querySelector('.sidebar');
        const overlay = document.getElementById('sidebar-overlay');
        const btn = document.getElementById('btn-hamburger');
        if (sidebar) sidebar.classList.remove('active');
        if (overlay) overlay.classList.remove('active');
        if (btn) btn.classList.remove('active');
    },

    // Command Palette Logic
    initCommandPalette() {
        document.addEventListener('keydown', (e) => {
            if ((e.ctrlKey || e.metaKey) && e.key.toLowerCase() === 'k') {
                e.preventDefault();
                this.toggleCommandPalette();
            } else if (e.key === 'Escape') {
                this.closeCommandPalette();
            }
        });
    },

    openCommandPalette() {
        const overlay = document.getElementById('cmd-overlay');
        const input = document.getElementById('cmd-input');
        if (overlay) overlay.classList.add('active');
        if (input) {
            input.value = '';
            input.focus();
            this.handleCommandSearch('');
        }
    },

    closeCommandPalette() {
        const overlay = document.getElementById('cmd-overlay');
        if (overlay) overlay.classList.remove('active');
    },

    toggleCommandPalette() {
        const overlay = document.getElementById('cmd-overlay');
        if (overlay && overlay.classList.contains('active')) {
            this.closeCommandPalette();
        } else {
            this.openCommandPalette();
        }
    },

    async handleCommandSearch(query) {
        const container = document.getElementById('cmd-results');
        if (!container) return;

        const q = (query || '').trim().toLowerCase();
        if (!q) {
            container.innerHTML = `<div class="cmd-empty-hint">Escribe para buscar bodegas, productos o movimientos en tiempo real...</div>`;
            return;
        }

        try {
            const [bodegas, productos, movimientos] = await Promise.all([
                BodegaService.getAll().catch(() => []),
                ProductoService.getAll().catch(() => []),
                MovimientoService.getAll().catch(() => [])
            ]);

            const matchedBodegas = bodegas.filter(b => (b.nombre || '').toLowerCase().includes(q) || (b.ubicacion || '').toLowerCase().includes(q));
            const matchedProductos = productos.filter(p => (p.nombre || '').toLowerCase().includes(q) || (p.categoriaNombre || '').toLowerCase().includes(q) || (p.descripcion || '').toLowerCase().includes(q));
            const matchedMovimientos = movimientos.filter(m => 
                `#${m.id}`.includes(q) ||
                (m.tipoMovimiento || '').toLowerCase().includes(q) ||
                (m.usuarioNombre || '').toLowerCase().includes(q) ||
                (m.detalles && m.detalles.some(d => (d.productoNombre || '').toLowerCase().includes(q)))
            );

            if (matchedBodegas.length === 0 && matchedProductos.length === 0 && matchedMovimientos.length === 0) {
                container.innerHTML = `<div class="cmd-empty-hint">No se encontraron resultados para "<strong>${q}</strong>"</div>`;
                return;
            }

            let html = '';

            if (matchedBodegas.length > 0) {
                html += `<div class="cmd-group-title">🏢 Bodegas (${matchedBodegas.length})</div>`;
                html += matchedBodegas.slice(0, 4).map(b => `
                    <div class="cmd-item" onclick="App.closeCommandPalette(); window.location.hash='#/bodegas';">
                        <div class="cmd-item-left">
                            <span style="font-size:16px;">🏬</span>
                            <div>
                                <div class="cmd-item-title">${b.nombre}</div>
                                <div class="cmd-item-sub">${b.ubicacion || 'Nivel Nacional'}</div>
                            </div>
                        </div>
                        <span class="badge badge-info">Bodega</span>
                    </div>
                `).join('');
            }

            if (matchedProductos.length > 0) {
                html += `<div class="cmd-group-title">📦 Productos (${matchedProductos.length})</div>`;
                html += matchedProductos.slice(0, 4).map(p => `
                    <div class="cmd-item" onclick="App.closeCommandPalette(); App.openProductoDrawer(${p.id});">
                        <div class="cmd-item-left">
                            <span style="font-size:16px;">📦</span>
                            <div>
                                <div class="cmd-item-title">${p.nombre}</div>
                                <div class="cmd-item-sub">Stock: ${p.stock} un. · $${Number(p.precio).toLocaleString('es-CO')}</div>
                            </div>
                        </div>
                        <span class="badge badge-secondary">Ver Ficha →</span>
                    </div>
                `).join('');
            }

            if (matchedMovimientos.length > 0) {
                html += `<div class="cmd-group-title">🔄 Movimientos (${matchedMovimientos.length})</div>`;
                html += matchedMovimientos.slice(0, 4).map(m => {
                    let badgeClass = 'badge-info';
                    if (m.tipoMovimiento === 'ENTRADA') badgeClass = 'badge-success';
                    if (m.tipoMovimiento === 'SALIDA') badgeClass = 'badge-danger';
                    if (m.tipoMovimiento === 'TRANSFERENCIA') badgeClass = 'badge-warning';

                    return `
                        <div class="cmd-item" onclick="App.closeCommandPalette(); App.openMovimientoDrawer(${m.id});">
                            <div class="cmd-item-left">
                                <span style="font-size:16px;">📋</span>
                                <div>
                                    <div class="cmd-item-title">Movimiento #${m.id}</div>
                                    <div class="cmd-item-sub">${m.usuarioNombre || 'Sistema'} · ${m.fecha ? new Date(m.fecha).toLocaleDateString() : ''}</div>
                                </div>
                            </div>
                            <span class="badge ${badgeClass}">${m.tipoMovimiento}</span>
                        </div>
                    `;
                }).join('');
            }

            container.innerHTML = html;
        } catch (err) {
            console.error('Error en búsqueda Command Palette:', err);
        }
    },



    // Slide-Over Drawer Logic
    openDrawer(title, badgeText = 'DETALLE', badgeClass = 'badge-info', htmlContent = '') {
        const overlay = document.getElementById('drawer-overlay');
        const titleEl = document.getElementById('drawer-title');
        const badgeEl = document.getElementById('drawer-badge');
        const bodyEl  = document.getElementById('drawer-body');

        if (titleEl) titleEl.innerText = title;
        if (badgeEl) {
            badgeEl.innerText = badgeText;
            badgeEl.className = `badge ${badgeClass}`;
        }
        if (bodyEl) bodyEl.innerHTML = htmlContent;

        if (overlay) overlay.classList.add('active');
    },

    closeDrawer() {
        const overlay = document.getElementById('drawer-overlay');
        if (overlay) overlay.classList.remove('active');
    },

    async openMovimientoDrawer(movimientoId) {
        try {
            const m = await MovimientoService.getById(movimientoId);
            if (!m) return;

            let badgeClass = 'badge-info';
            if (m.tipoMovimiento === 'ENTRADA') badgeClass = 'badge-success';
            if (m.tipoMovimiento === 'SALIDA') badgeClass = 'badge-danger';
            if (m.tipoMovimiento === 'TRANSFERENCIA') badgeClass = 'badge-warning';

            const fecha = m.fecha ? new Date(m.fecha).toLocaleString() : '-';

            const detallesHtml = (m.detalles && m.detalles.length > 0)
                ? m.detalles.map(d => `
                    <div style="display:flex; justify-content:space-between; align-items:center; padding: 10px 0; border-bottom: 1px solid var(--border);">
                        <div>
                            <strong style="color:var(--text); font-size:14px;">${d.productoNombre}</strong>
                            <div style="font-size:11px; color:var(--text-muted); font-family:var(--font-mono);">ID Producto: #${d.productoId}</div>
                        </div>
                        <span class="badge badge-secondary" style="font-size:13px; font-weight:600;">${d.cantidad} unidades</span>
                    </div>
                `).join('')
                : `<p style="color:var(--text-muted);">No hay detalles específicos registrados.</p>`;

            const htmlContent = `
                <div class="drawer-card">
                    <div class="drawer-field">
                        <div class="drawer-field-label">ID Movimiento</div>
                        <div class="drawer-field-value" style="font-family:var(--font-mono); color:var(--accent);">#${m.id}</div>
                    </div>
                    <div class="drawer-field" style="margin-top:10px;">
                        <div class="drawer-field-label">Fecha y Hora de Registro</div>
                        <div class="drawer-field-value">${fecha}</div>
                    </div>
                    <div class="drawer-field" style="margin-top:10px;">
                        <div class="drawer-field-label">Usuario Responsable</div>
                        <div class="drawer-field-value">${m.usuarioNombre || 'Sistema'}</div>
                    </div>
                </div>

                <div class="drawer-card">
                    <div style="display:grid; grid-template-columns:1fr 1fr; gap:12px;">
                        <div class="drawer-field">
                            <div class="drawer-field-label">Bodega Origen</div>
                            <div class="drawer-field-value">${m.bodegaOrigen || '— (Entrada directa)'}</div>
                        </div>
                        <div class="drawer-field">
                            <div class="drawer-field-label">Bodega Destino</div>
                            <div class="drawer-field-value">${m.bodegaDestino || '— (Salida directa)'}</div>
                        </div>
                    </div>
                </div>

                <div class="drawer-card">
                    <div class="drawer-field-label" style="margin-bottom:10px;">Items e Inventario Afectado</div>
                    ${detallesHtml}
                </div>

                <div class="drawer-card">
                    <div class="drawer-field-label">Observaciones y Notas Operativas</div>
                    <div class="drawer-field-value" style="font-size:13px; color:var(--text-muted); line-height:1.5;">
                        ${m.observaciones || 'Sin observaciones registradas para este movimiento.'}
                    </div>
                </div>
            `;

            this.openDrawer(`Movimiento #${m.id}`, m.tipoMovimiento, badgeClass, htmlContent);
        } catch (err) {
            Toast.error('No se pudo cargar la información del movimiento');
        }
    },

    async openProductoDrawer(productoId) {
        try {
            const [p, inventario] = await Promise.all([
                ProductoService.getById(productoId),
                ProductoService.getInventario(productoId).catch(() => [])
            ]);
            if (!p) return;

            const lowStock = p.stock < (p.stockMinimo || 10);
            const stockBadge = lowStock ? 'badge-danger' : 'badge-success';

            const catName = p.categoriaNombre || p.categoria || 'General';
            let catBadgeClass = 'badge-info';
            const normCat = catName.toLowerCase();
            if (normCat.includes('mobil') || normCat.includes('muebl')) catBadgeClass = 'badge-cat-mobiliario';
            else if (normCat.includes('electr') || normCat.includes('tech')) catBadgeClass = 'badge-cat-electronica';
            else if (normCat.includes('perifer') || normCat.includes('accesor')) catBadgeClass = 'badge-cat-perifericos';
            else if (normCat.includes('papel')) catBadgeClass = 'badge-cat-papeleria';

            let bodegasHtml = '';
            if (inventario && inventario.length > 0) {
                bodegasHtml = inventario.map(inv => `
                    <span class="badge badge-bodega">
                        🏢 ${inv.bodegaNombre}: <strong>${inv.stockActual} u.</strong>
                    </span>
                `).join('');
            } else if (p.bodegaNombre && p.bodegaNombre !== 'Sin asignar') {
                bodegasHtml = `<span class="badge badge-bodega">🏢 ${p.bodegaNombre}</span>`;
            } else {
                bodegasHtml = `<span class="badge badge-warning">⚠️ Sin asignación de bodega</span>`;
            }

            const htmlContent = `
                <div class="drawer-card" style="text-align:center; padding: 22px 16px; background: linear-gradient(180deg, rgba(255,255,255,0.03), transparent);">
                    <div style="font-size:2.8rem; margin-bottom:10px; filter: drop-shadow(0 4px 10px rgba(0,0,0,0.3));">📦</div>
                    <h4 style="font-family:var(--font-display); font-size:1.5rem; color:var(--text); margin:0 0 8px; letter-spacing:0.02em;">${p.nombre}</h4>
                    <span class="badge ${catBadgeClass}">${catName}</span>
                </div>

                <div class="drawer-card">
                    <div style="display:grid; grid-template-columns:1fr 1fr; gap:16px;">
                        <div class="drawer-field">
                            <div class="drawer-field-label">Stock Total</div>
                            <div class="drawer-field-value">
                                <span class="badge ${stockBadge}" style="font-size:12.5px; font-weight:700;">
                                    ${p.stock} UNIDADES ${lowStock ? '⚠️ BAJO' : ''}
                                </span>
                            </div>
                        </div>
                        <div class="drawer-field">
                            <div class="drawer-field-label">Precio Unitario</div>
                            <div class="drawer-field-value" style="color:var(--accent); font-family:var(--font-mono); font-size:16px; font-weight:700;">
                                $${Number(p.precio).toLocaleString('es-CO')}
                            </div>
                        </div>
                    </div>
                </div>

                <div class="drawer-card">
                    <div class="drawer-field">
                        <div class="drawer-field-label" style="margin-bottom:8px;">Bodegas Asignadas</div>
                        <div style="display:flex; flex-wrap:wrap; gap:8px;">
                            ${bodegasHtml}
                        </div>
                    </div>
                </div>

                <div class="drawer-card">
                    <div class="drawer-field-label">Descripción del Producto</div>
                    <div class="drawer-field-value" style="font-size:13px; color:var(--text-muted); line-height:1.6;">
                        ${p.descripcion || 'Sin descripción detallada disponible.'}
                    </div>
                </div>
            `;

            this.openDrawer(p.nombre, 'PRODUCTO', catBadgeClass, htmlContent);
        } catch (err) {
            console.error('Error cargando producto:', err);
            Toast.error('No se pudo cargar la ficha del producto');
        }
    },

    togglePasswordVisibility(inputId, btn) {
        const input = document.getElementById(inputId);
        if (!input) return;
        const iconSpan = btn.querySelector('.eye-icon');
        if (input.type === 'password') {
            input.type = 'text';
            if (iconSpan) iconSpan.textContent = '🙈';
        } else {
            input.type = 'password';
            if (iconSpan) iconSpan.textContent = '👁️';
        }
    },

    initCapsLockDetectors() {
        const checkCaps = (e, alertId) => {
            const alertEl = document.getElementById(alertId);
            if (!alertEl) return;
            const isCaps = e.getModifierState && e.getModifierState('CapsLock');
            alertEl.style.display = isCaps ? 'inline-flex' : 'none';
        };

        ['login-password', 'reg-password'].forEach(id => {
            const input = document.getElementById(id);
            const alertId = id === 'login-password' ? 'caps-alert-login' : 'caps-alert-reg';
            if (input) {
                input.addEventListener('keyup', (e) => checkCaps(e, alertId));
                input.addEventListener('keydown', (e) => checkCaps(e, alertId));
                input.addEventListener('blur', () => {
                    const alertEl = document.getElementById(alertId);
                    if (alertEl) alertEl.style.display = 'none';
                });
            }
        });
    },

    bindEvents() {
        // Formulario Login
        const loginForm = document.getElementById('login-form');
        if (loginForm) {
            loginForm.addEventListener('submit', async (e) => {
                e.preventDefault();
                const email = document.getElementById('login-email').value;
                const pass = document.getElementById('login-password').value;
                try {
                    await AuthService.login(email, pass);
                    Toast.success('¡Bienvenido a LogiTrack S.A.!');
                    this.updateUserInfo();
                    Router.navigate('dashboard');
                } catch (err) {
                    Toast.error(err.message || 'Error de autenticación: verifica tu correo y contraseña');
                }
            });
        }

        // Formulario Registro con Auto-Login Inteligente
        const regForm = document.getElementById('register-form');
        if (regForm) {
            regForm.addEventListener('submit', async (e) => {
                e.preventDefault();
                const nombre = document.getElementById('reg-nombre').value;
                const apellido = document.getElementById('reg-apellido').value;
                const email = document.getElementById('reg-email').value;
                const pass = document.getElementById('reg-password').value;
                const rol = document.getElementById('reg-rol').value;

                try {
                    await AuthService.register(nombre, apellido, email, pass, rol);
                    Toast.success('¡Usuario registrado con éxito! Iniciando sesión...');
                    
                    // Pre-llenar campos e iniciar sesión automáticamente
                    const loginEmailEl = document.getElementById('login-email');
                    const loginPassEl = document.getElementById('login-password');
                    if (loginEmailEl) loginEmailEl.value = email;
                    if (loginPassEl) loginPassEl.value = pass;

                    await AuthService.login(email, pass);
                    this.updateUserInfo();
                    Router.navigate('dashboard');
                } catch (err) {
                    Toast.error(err.message || 'Error al registrar usuario');
                }
            });
        }

        // Formulario Bodega Modal
        const bodegaForm = document.getElementById('form-bodega');
        if (bodegaForm) {
            bodegaForm.addEventListener('submit', async (e) => {
                e.preventDefault();
                const data = {
                    nombre: document.getElementById('bodega-nombre').value,
                    ubicacion: document.getElementById('bodega-ubicacion').value,
                    capacidad: parseInt(document.getElementById('bodega-capacidad').value),
                    encargado: document.getElementById('bodega-encargado').value,
                    activo: document.getElementById('bodega-activo').checked
                };
                await BodegaModuleController.save(data);
            });
        }

        // Formulario Producto Modal
        const productoForm = document.getElementById('form-producto');
        if (productoForm) {
            productoForm.addEventListener('submit', async (e) => {
                e.preventDefault();
                const catIdVal = document.getElementById('producto-categoria-id').value;
                const bodegaIdVal = document.getElementById('producto-bodega-id')?.value;
                const data = {
                    nombre: document.getElementById('producto-nombre').value,
                    categoriaId: catIdVal ? parseInt(catIdVal) : null,
                    bodegaId: bodegaIdVal ? parseInt(bodegaIdVal) : null,
                    stock: parseInt(document.getElementById('producto-stock').value),
                    precio: parseFloat(document.getElementById('producto-precio').value),
                    descripcion: document.getElementById('producto-descripcion').value
                };
                await ProductoModuleController.save(data);
            });
        }

        // Formulario Categoria Modal
        const categoriaForm = document.getElementById('form-categoria');
        if (categoriaForm) {
            categoriaForm.addEventListener('submit', async (e) => {
                e.preventDefault();
                const data = {
                    nombre: document.getElementById('categoria-nombre').value,
                    descripcion: document.getElementById('categoria-descripcion').value
                };
                await ProductoModuleController.guardarCategoria(data);
            });
        }

        // Formulario Movimiento Modal
        const movimientoForm = document.getElementById('form-movimiento');
        if (movimientoForm) {
            movimientoForm.addEventListener('submit', async (e) => {
                e.preventDefault();
                const tipo = document.getElementById('mov-tipo').value;
                const bodegaOrigenId = document.getElementById('mov-origen').value;
                const bodegaDestinoId = document.getElementById('mov-destino').value;
                const productoId = parseInt(document.getElementById('mov-producto').value);
                const cantidad = parseInt(document.getElementById('mov-cantidad').value);
                const observaciones = document.getElementById('mov-obs').value;

                const data = {
                    tipoMovimiento: tipo,
                    bodegaOrigenId: bodegaOrigenId ? parseInt(bodegaOrigenId) : null,
                    bodegaDestinoId: bodegaDestinoId ? parseInt(bodegaDestinoId) : null,
                    observaciones: observaciones,
                    detalles: [
                        {
                            productoId: productoId,
                            cantidad: cantidad,
                            precioUnitario: 0.0
                        }
                    ]
                };
                await MovimientoModuleController.registrar(data);
            });
        }

        // Formulario Usuario Modal
        const usuarioForm = document.getElementById('form-usuario');
        if (usuarioForm) {
            usuarioForm.addEventListener('submit', async (e) => {
                e.preventDefault();
                const data = {
                    nombre: document.getElementById('usuario-nombre').value,
                    apellido: document.getElementById('usuario-apellido').value,
                    email: document.getElementById('usuario-email').value,
                    password: document.getElementById('usuario-password').value,
                    rol: document.getElementById('usuario-rol').value,
                    activo: document.getElementById('usuario-activo').checked
                };
                await UsuarioModuleController.save(data);
            });
        }
    },

    toggleAuthMode(mode) {
        const loginCard = document.getElementById('auth-login-card');
        const regCard = document.getElementById('auth-register-card');
        if (mode === 'register') {
            loginCard.style.display = 'none';
            regCard.style.display = 'block';
        } else {
            loginCard.style.display = 'block';
            regCard.style.display = 'none';
        }
    },

    fillDemoLogin(role) {
        const emailEl = document.getElementById('login-email');
        const passEl = document.getElementById('login-password');
        if (role === 'admin') {
            if (emailEl) emailEl.value = 'admin@logitrack.com';
            if (passEl) passEl.value = 'admin123';
        } else if (role === 'gerente') {
            if (emailEl) emailEl.value = 'sofia@logitrack.com';
            if (passEl) passEl.value = 'empleado123';
        } else if (role === 'supervisor') {
            if (emailEl) emailEl.value = 'laura@logitrack.com';
            if (passEl) passEl.value = 'empleado123';
        } else if (role === 'compras') {
            if (emailEl) emailEl.value = 'pedro@logitrack.com';
            if (passEl) passEl.value = 'empleado123';
        } else {
            if (emailEl) emailEl.value = 'carlos@logitrack.com';
            if (passEl) passEl.value = 'empleado123';
        }
    },

    updateUserInfo() {
        const user = AuthService.getCurrentUser();
        const userNameEl    = document.getElementById('user-display-name');
        const userRoleEl    = document.getElementById('user-display-role');
        const userAvatarEl  = document.getElementById('user-avatar-initials');
        if (user && userNameEl) {
            const displayName = user.nombre
                ? `${user.nombre} ${user.apellido || ''}`.trim()
                : user.email;
            userNameEl.innerText = displayName;
            userRoleEl.innerText = (user.rol || 'EMPLEADO') + ' · LOGITRACK';
            if (userAvatarEl) {
                const parts = displayName.split(' ');
                userAvatarEl.textContent = ((parts[0]?.[0] || '') + (parts[1]?.[0] || '')).toUpperCase() || 'LT';
            }
        }

        const role = user ? user.rol : 'EMPLEADO';
        const isAdmin = role === 'ADMIN';
        const isGerente = role === 'GERENTE_LOGISTICA';
        const isSupervisor = role === 'SUPERVISOR';
        const isCompras = role === 'JEFE_COMPRAS';

        const canSeeControl = isAdmin || isSupervisor || isGerente;
        const canManage = isAdmin || isSupervisor || isCompras;
        const canRegisterMovement = isAdmin || isSupervisor || isCompras || role === 'EMPLEADO';

        // Mostrar / Ocultar grupo Administración (Usuarios) -> Solo ADMIN
        document.querySelectorAll('.nav-admin-only').forEach(el => {
            el.style.display = isAdmin ? '' : 'none';
        });

        // Mostrar / Ocultar grupo Control (Auditoría y Reportes) -> ADMIN, SUPERVISOR o GERENTE_LOGISTICA
        document.querySelectorAll('.nav-control-only').forEach(el => {
            el.style.display = canSeeControl ? '' : 'none';
        });

        // Mostrar / Ocultar botones de creación (+ Nueva Bodega, + Nuevo Producto) -> ADMIN, SUPERVISOR o JEFE_COMPRAS
        document.querySelectorAll('.btn-manage-only').forEach(el => {
            el.style.display = canManage ? '' : 'none';
        });

        // Mostrar / Ocultar botón (+ Registrar Movimiento) -> Deshabilitado para GERENTE_LOGISTICA
        document.querySelectorAll('.btn-op-only').forEach(el => {
            el.style.display = canRegisterMovement ? '' : 'none';
        });
    },

    async loadViewData(view) {
        try {
            if (view === 'dashboard') {
                this.loadDashboardData();
            } else if (view === 'reportes') {
                await ReporteModuleController.load();
            } else if (view === 'bodegas') {
                await BodegaModuleController.load();
            } else if (view === 'productos') {
                await ProductoModuleController.load();
            } else if (view === 'movimientos') {
                await MovimientoModuleController.load();
            } else if (view === 'auditorias') {
                await AuditoriaModuleController.load();
            } else if (view === 'usuarios') {
                await UsuarioModuleController.load();
            }
        } catch (err) {
            console.error(`Error al cargar datos de vista [${view}]:`, err);
        }
    },

    async loadDashboardData() {
        try {
            const [bodegas, productos, movimientos] = await Promise.all([
                BodegaService.getAll().catch(() => []),
                ProductoService.getAll().catch(() => []),
                MovimientoService.getAll().catch(() => [])
            ]);

            const bodegasActivas = bodegas.filter(b => b.activo !== false);

            const totalBodegasEl = document.getElementById('metric-total-bodegas');
            if (totalBodegasEl) totalBodegasEl.innerText = String(bodegasActivas.length || 0).padStart(2, '0');

            const totalMovimientosEl = document.getElementById('metric-total-movimientos');
            if (totalMovimientosEl) totalMovimientosEl.innerText = movimientos.length || 0;

            // Calcular el inventario disponible EXCLUSIVAMENTE en bodegas activas
            const inventariosActivos = await Promise.all(
                bodegasActivas.map(b => BodegaService.getInventario(b.id).catch(() => []))
            );

            let stockTotalActivo = 0;
            inventariosActivos.forEach(invList => {
                (invList || []).forEach(item => {
                    stockTotalActivo += (item.stockActual || 0);
                });
            });

            // Si las bodegas activas no tienen inventario mapeado aun, usar stock total de productos activos
            if (stockTotalActivo === 0 && bodegasActivas.length > 0) {
                stockTotalActivo = productos.reduce((sum, p) => sum + (p.stock || 0), 0);
            }

            const stockEl = document.getElementById('metric-stock-total');
            if (stockEl) stockEl.innerText = stockTotalActivo.toLocaleString('es-CO');

            const lowStockCount = productos.filter(p => p.stock < 10).length;
            const bajoStockMetricEl = document.getElementById('metric-bajo-stock');
            if (bajoStockMetricEl) bajoStockMetricEl.innerText = String(lowStockCount).padStart(2, '0');

            DashboardRenderer.renderBodegasCards(bodegasActivas);
            DashboardRenderer.renderMovimientosRecientes(movimientos);
            DashboardRenderer.renderBajoStockList(productos.filter(p => p.stock < 10));

            if (typeof window.animateGauges === 'function') window.animateGauges();
        } catch (err) {
            console.error('Error cargando dashboard:', err);
        }
    },

    async loadCharts() {
        try {
            if (typeof Charts !== 'undefined') {
                Charts.mostrarEstadoCargando();
                const reporte = await ReporteService.getResumenGeneral().catch(() => null);
                if (reporte && reporte.stockPorBodega) {
                    Charts.renderStockBodegas(reporte.stockPorBodega);
                }
                if (reporte && reporte.productosMasMovidos) {
                    Charts.renderProductosMovidos(reporte.productosMasMovidos);
                }
                const movimientos = await MovimientoService.getAll().catch(() => []);
                Charts.renderTiposMovimiento(movimientos);
            }
        } catch (e) {
            console.error('Error cargando gráficas:', e);
        }
    },

    // Shortcuts para modales
    openModal(modalId) {
        const modal = document.getElementById(modalId);
        if (modal) modal.classList.add('active');
        if (modalId === 'modal-movimiento') {
            MovimientoModuleController.populateSelects();
        }
    },

    closeModal(modalId) {
        const modal = document.getElementById(modalId);
        if (modal) modal.classList.remove('active');
    },

    openBodegaModal() {
        BodegaModuleController.openCreateModal();
    },

    openProductoModal() {
        ProductoModuleController.openCreateModal();
    }
};

document.addEventListener('DOMContentLoaded', () => {
    App.init();
});

window.App = App;
