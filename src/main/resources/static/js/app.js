/* ==========================================
   LogiTrack S.A. - Main Application Orchestrator
   ========================================== */

const App = {
    init() {
        console.log('LogiTrack S.A. Frontend Orquestado Inicializado...');
        this.bindEvents();
        this.initCapsLockDetectors();
        this.initGoogleSignIn();
        this.initCommandPalette();
        this.loadHeroStats();
        Router.init();
        this.updateUserInfo();
        this.initNotifications();
    },

    // Carga de estadísticas en tiempo real para el hero del login [H-014]
    async loadHeroStats() {
        try {
            const stats = await ApiService.get('/config/stats');
            if (stats) {
                const bodEl = document.getElementById('hero-stat-bodegas');
                const movEl = document.getElementById('hero-stat-movimientos');
                const audEl = document.getElementById('hero-stat-auditorias');

                if (bodEl && stats.bodegasActivas !== undefined) {
                    bodEl.innerHTML = `${stats.bodegasActivas} <span>bod</span>`;
                }
                if (movEl && stats.totalMovimientos !== undefined) {
                    const movCount = stats.totalMovimientos >= 1000
                        ? (stats.totalMovimientos / 1000).toFixed(1).replace('.', ',') + ' <span>K</span>'
                        : `${stats.totalMovimientos} <span>mov</span>`;
                    movEl.innerHTML = movCount;
                }
                if (audEl && stats.auditoriaCoverage) {
                    audEl.innerHTML = `${stats.auditoriaCoverage}`;
                }
            }
        } catch (e) {
            // Silencioso si no está disponible
        }
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
        if (!sidebar) return;
        sidebar.classList.remove('active');
        if (overlay) overlay.classList.remove('active');
        if (btn) btn.classList.remove('active');
    },

    openGoogleModal(email, nombre, apellido) {
        const emailEl = document.getElementById('google-complete-email');
        const nombreEl = document.getElementById('google-complete-nombre');
        const apellidoEl = document.getElementById('google-complete-apellido');
        const modal = document.getElementById('modal-google-complete');

        if (emailEl) emailEl.value = email || '';
        if (nombreEl) nombreEl.value = nombre || '';
        if (apellidoEl) apellidoEl.value = apellido || '';
        if (modal) modal.classList.add('active');
    },

    closeGoogleModal() {
        const modal = document.getElementById('modal-google-complete');
        if (modal) modal.classList.remove('active');
    },

    // Google Sign-In Initialization
    initGoogleSignIn() {
        window.handleGoogleCredentialResponse = async (response) => {
            if (!response || !response.credential) return;
            try {
                const res = await AuthService.loginWithGoogle(response.credential);
                if (res && res.registrado) {
                    Toast.success('¡Sesión iniciada exitosamente!');
                    this.updateUserInfo();
                    Router.navigate('dashboard');
                } else if (res && !res.registrado) {
                    Toast.info('Por favor confirma tus datos, asigna tu rol y define tu contraseña');
                    this.openGoogleModal(res.email, res.nombre, res.apellido);
                }
            } catch (err) {
                Toast.error(err.message || 'Error al autenticar con Google');
            }
        };

        const renderGoogleBtns = async () => {
            if (typeof google !== 'undefined' && google.accounts && google.accounts.id) {
                let googleClientId = "";
                try {
                    const pubConfig = await ApiService.get('/config/public');
                    if (pubConfig && pubConfig.googleClientId) {
                        googleClientId = pubConfig.googleClientId;
                    }
                } catch (e) {
                    console.warn('No se pudo cargar el Client ID de Google OAuth2');
                }

                if (!googleClientId) {
                    googleClientId = "1056581979401-4n88v213h468n4613n89.apps.googleusercontent.com";
                }

                google.accounts.id.initialize({
                    client_id: googleClientId,
                    callback: window.handleGoogleCredentialResponse,
                    auto_select: false,
                    ux_mode: 'popup',
                    use_fedcm_for_prompt: true
                });

                const btnLogin = document.getElementById('google-btn-login');
                if (btnLogin) {
                    google.accounts.id.renderButton(btnLogin, {
                        theme: "outline",
                        size: "large",
                        text: "signin_with",
                        shape: "rectangular",
                        width: 280
                    });
                }

                const btnRegister = document.getElementById('google-btn-register');
                if (btnRegister) {
                    google.accounts.id.renderButton(btnRegister, {
                        theme: "outline",
                        size: "large",
                        text: "signup_with",
                        shape: "rectangular",
                        width: 280
                    });
                }
            } else {
                setTimeout(renderGoogleBtns, 400);
            }
        };

        renderGoogleBtns();
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
                html += `<div class="cmd-group-title">BODEGAS (${matchedBodegas.length})</div>`;
                html += matchedBodegas.slice(0, 4).map(b => `
                    <div class="cmd-item" onclick="App.closeCommandPalette(); window.location.hash='#/bodegas';">
                        <div class="cmd-item-left">
                            <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="var(--accent)" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M3 21h18M3 7v14M21 7v14M6 21V11m4 10V11m4 10V11m4 10V11M3 7l9-4 9 4"></path></svg>
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
                html += `<div class="cmd-group-title">PRODUCTOS (${matchedProductos.length})</div>`;
                html += matchedProductos.slice(0, 4).map(p => `
                    <div class="cmd-item" onclick="App.closeCommandPalette(); App.openProductoDrawer(${p.id});">
                        <div class="cmd-item-left">
                            <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="var(--accent-2)" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M21 16V8a2 2 0 0 0-1-1.73l-7-4a2 2 0 0 0-2 0l-7 4A2 2 0 0 0 3 8v8a2 2 0 0 0 1 1.73l7 4a2 2 0 0 0 2 0l7-4A2 2 0 0 0 21 16z"></path><polyline points="3.27 6.96 12 12.01 20.73 6.96"></polyline><line x1="12" y1="22.08" x2="12" y2="12"></line></svg>
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
                html += `<div class="cmd-group-title">MOVIMIENTOS (${matchedMovimientos.length})</div>`;
                html += matchedMovimientos.slice(0, 4).map(m => {
                    let badgeClass = 'badge-info';
                    if (m.tipoMovimiento === 'ENTRADA') badgeClass = 'badge-success';
                    if (m.tipoMovimiento === 'SALIDA') badgeClass = 'badge-danger';
                    if (m.tipoMovimiento === 'TRANSFERENCIA') badgeClass = 'badge-warning';

                    return `
                        <div class="cmd-item" onclick="App.closeCommandPalette(); App.openMovimientoDrawer(${m.id});">
                            <div class="cmd-item-left">
                                <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="var(--text-muted)" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M14 2H6a2 2 0 0 0-2 2v16a2 2 0 0 0 2 2h12a2 2 0 0 0 2-2V8z"></path><polyline points="14 2 14 8 20 8"></polyline><line x1="16" y1="13" x2="8" y2="13"></line><line x1="16" y1="17" x2="8" y2="17"></line><polyline points="10 9 9 9 8 9"></polyline></svg>
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
    openDrawer(title = 'Detalle', badgeText = 'DETALLE', badgeClass = 'badge-info', htmlContent = '') {
        const overlay = document.getElementById('drawer-overlay');
        const titleEl = document.getElementById('drawer-title');
        const badgeEl = document.getElementById('drawer-badge');
        const bodyEl  = document.getElementById('drawer-body');

        if (titleEl && title) titleEl.innerText = title;
        if (badgeEl && badgeText) {
            badgeEl.innerText = badgeText;
            badgeEl.className = `badge ${badgeClass}`;
        }
        if (bodyEl && htmlContent) bodyEl.innerHTML = htmlContent;

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

                ${(m.proveedorNombre || m.clienteNombre) ? `
                    <div class="drawer-card">
                        <div class="drawer-field">
                            <div class="drawer-field-label">${m.proveedorNombre ? 'Proveedor de Mercancía' : 'Cliente Receptor / Despacho'}</div>
                            <div class="drawer-field-value" style="font-weight:600; color:var(--text-bright);">
                                ${m.proveedorNombre ? m.proveedorNombre : (m.clienteNombre || "Operación interna")}
                            </div>
                        </div>
                    </div>
                ` : ''}

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
                        <span class="status-indicator completed" style="margin-right:4px;"></span> ${inv.bodegaNombre}: <strong>${inv.stockActual} u.</strong>
                    </span>
                `).join('');
            } else if (p.bodegaNombre && p.bodegaNombre !== 'Sin asignar') {
                bodegasHtml = `<span class="badge badge-bodega"><span class="status-indicator completed" style="margin-right:4px;"></span> ${p.bodegaNombre}</span>`;
            } else {
                bodegasHtml = `<span class="badge badge-warning">Sin asignación de bodega</span>`;
            }

            const htmlContent = `
                <div class="drawer-card" style="text-align:center; padding: 22px 16px; background: linear-gradient(180deg, rgba(255,255,255,0.03), transparent);">
                    <div style="display:inline-flex; align-items:center; justify-content:center; width:56px; height:56px; border-radius:12px; background:rgba(255,106,43,0.1); border:1px solid rgba(255,106,43,0.25); color:var(--accent); margin-bottom:12px; box-shadow:0 0 16px rgba(255,106,43,0.2);"><svg width="28" height="28" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.8" stroke-linecap="round" stroke-linejoin="round"><path d="M21 16V8a2 2 0 0 0-1-1.73l-7-4a2 2 0 0 0-2 0l-7 4A2 2 0 0 0 3 8v8a2 2 0 0 0 1 1.73l7 4a2 2 0 0 0 2 0l7-4A2 2 0 0 0 21 16z"></path><polyline points="3.27 6.96 12 12.01 20.73 6.96"></polyline><line x1="12" y1="22.08" x2="12" y2="12"></line></svg></div>
                    <h4 style="font-family:var(--font-display); font-size:1.5rem; color:var(--text); margin:0 0 8px; letter-spacing:0.02em;">${p.nombre}</h4>
                    <span class="badge ${catBadgeClass}">${catName}</span>
                </div>

                <div class="drawer-card">
                    <div style="display:grid; grid-template-columns:1fr 1fr; gap:16px;">
                        <div class="drawer-field">
                            <div class="drawer-field-label">Stock Total</div>
                            <div class="drawer-field-value">
                                <span class="badge ${stockBadge}" style="font-size:12.5px; font-weight:700;">
                                    ${p.stock} UNIDADES ${lowStock ? '· BAJO' : ''}
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
        
        const isPassword = input.type === 'password';
        input.type = isPassword ? 'text' : 'password';

        if (isPassword) {
            btn.classList.add('peek-active');
            btn.setAttribute('title', 'Ocultar contraseña');
            btn.innerHTML = `
                <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.2" stroke-linecap="round" stroke-linejoin="round" class="eye-svg peek-eye">
                    <path d="M1 12s4-8 11-8 11 8 11 8-4 8-11 8-11-8-11-8z"></path>
                    <circle cx="12" cy="12" r="3" fill="var(--accent)"></circle>
                </svg>`;
        } else {
            btn.classList.remove('peek-active');
            btn.setAttribute('title', 'Mostrar contraseña');
            btn.innerHTML = `
                <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" class="eye-svg">
                    <path d="M17.94 17.94A10.07 10.07 0 0 1 12 20c-7 0-11-8-11-8a18.45 18.45 0 0 1 5.06-5.94M9.9 4.24A9.12 9.12 0 0 1 12 4c7 0 11 8 11 8a18.5 18.5 0 0 1-2.16 3.19m-6.72-1.07a3 3 0 1 1-4.24-4.24"></path>
                    <line x1="1" y1="1" x2="23" y2="23"></line>
                </svg>`;
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

        // Formulario Completar Registro de Google
        const googleCompleteForm = document.getElementById('form-google-complete');
        if (googleCompleteForm) {
            googleCompleteForm.addEventListener('submit', async (e) => {
                e.preventDefault();
                const email = document.getElementById('google-complete-email').value;
                const nombre = document.getElementById('google-complete-nombre').value;
                const apellido = document.getElementById('google-complete-apellido').value;
                const rol = document.getElementById('google-complete-rol').value;
                const pass = document.getElementById('google-complete-password').value;

                try {
                    await AuthService.register(nombre, apellido, email, pass, rol);
                    Toast.success('¡Registro completado exitosamente! Iniciando sesión...');
                    await AuthService.login(email, pass);
                    this.closeGoogleModal();
                    this.updateUserInfo();
                    Router.navigate('dashboard');
                } catch (err) {
                    Toast.error(err.message || 'Error al finalizar el registro de Google');
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

                const provIdVal = document.getElementById('mov-proveedor')?.value;
                const cliIdVal = document.getElementById('mov-cliente')?.value;

                const data = {
                    tipoMovimiento: tipo,
                    bodegaOrigenId: bodegaOrigenId ? parseInt(bodegaOrigenId) : null,
                    bodegaDestinoId: bodegaDestinoId ? parseInt(bodegaDestinoId) : null,
                    proveedorId: (tipo === 'ENTRADA' && provIdVal) ? parseInt(provIdVal) : null,
                    clienteId: (tipo === 'SALIDA' && cliIdVal) ? parseInt(cliIdVal) : null,
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

        // Formulario Cliente Modal
        const clienteForm = document.getElementById('form-cliente');
        if (clienteForm) {
            clienteForm.addEventListener('submit', async (e) => {
                e.preventDefault();
                const data = {
                    nombre: document.getElementById('cliente-nombre').value,
                    ruc: document.getElementById('cliente-ruc').value,
                    telefono: document.getElementById('cliente-telefono').value,
                    email: document.getElementById('cliente-email').value,
                    direccion: document.getElementById('cliente-direccion').value
                };
                await ClienteModuleController.save(data);
            });
        }

        // Formulario Proveedor Modal
        const proveedorForm = document.getElementById('form-proveedor');
        if (proveedorForm) {
            proveedorForm.addEventListener('submit', async (e) => {
                e.preventDefault();
                const data = {
                    nombre: document.getElementById('proveedor-nombre').value,
                    ruc: document.getElementById('proveedor-ruc').value,
                    telefono: document.getElementById('proveedor-telefono').value,
                    email: document.getElementById('proveedor-email').value,
                    direccion: document.getElementById('proveedor-direccion').value
                };
                await ProveedorModuleController.save(data);
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

        // Formulario Ajuste Inventario Modal
        const ajusteForm = document.getElementById('form-ajuste');
        if (ajusteForm) {
            ajusteForm.addEventListener('submit', async (e) => {
                e.preventDefault();
                try {
                    const data = {
                        bodegaId: parseInt(document.getElementById('ajuste-bodega').value),
                        productoId: parseInt(document.getElementById('ajuste-producto').value),
                        tipoAjuste: document.getElementById('ajuste-tipo').value,
                        cantidadNueva: parseInt(document.getElementById('ajuste-cantidad-nueva').value),
                        justificacion: document.getElementById('ajuste-justificacion').value
                    };
                    const res = await fetch('/api/ajustes', {
                        method: 'POST',
                        headers: {
                            'Content-Type': 'application/json',
                            'Authorization': `Bearer ${AuthService.getToken()}`
                        },
                        body: JSON.stringify(data)
                    });
                    if (!res.ok) {
                        const err = await res.json().catch(() => ({}));
                        throw new Error(err.mensaje || 'Error al registrar ajuste');
                    }
                    Toast.success('Ajuste de inventario registrado exitosamente');
                    App.closeModal('modal-ajuste');
                    if (typeof AjusteController !== 'undefined') AjusteController.init();
                    else if (typeof ajusteController !== 'undefined') ajusteController.init();
                } catch (err) {
                    Toast.error(err.message || 'Error al registrar ajuste');
                }
            });
        }

        // Formulario Orden de Compra Modal
        const ocForm = document.getElementById('form-orden-compra');
        if (ocForm) {
            ocForm.addEventListener('submit', async (e) => {
                e.preventDefault();
                try {
                    const data = {
                        proveedorId: parseInt(document.getElementById('oc-proveedor').value),
                        bodegaDestinoId: parseInt(document.getElementById('oc-bodega').value),
                        fechaEntregaEsperada: document.getElementById('oc-fecha-entrega').value || null,
                        observaciones: document.getElementById('oc-observaciones').value,
                        detalles: [
                            {
                                productoId: parseInt(document.getElementById('oc-producto').value),
                                cantidad: parseInt(document.getElementById('oc-cantidad').value),
                                precioUnitario: parseFloat(document.getElementById('oc-precio').value)
                            }
                        ]
                    };
                    const res = await fetch('/api/ordenes-compra', {
                        method: 'POST',
                        headers: {
                            'Content-Type': 'application/json',
                            'Authorization': `Bearer ${AuthService.getToken()}`
                        },
                        body: JSON.stringify(data)
                    });
                    if (!res.ok) {
                        const err = await res.json().catch(() => ({}));
                        throw new Error(err.mensaje || 'Error al generar orden de compra');
                    }
                    Toast.success('Orden de compra generada exitosamente');
                    App.closeModal('modal-orden-compra');
                    if (typeof OrdenCompraController !== 'undefined') OrdenCompraController.init();
                    else if (typeof ordenCompraController !== 'undefined') ordenCompraController.init();
                } catch (err) {
                    Toast.error(err.message || 'Error al generar orden de compra');
                }
            });
        }

        // Formulario Lote Modal
        const loteForm = document.getElementById('form-lote');
        if (loteForm) {
            loteForm.addEventListener('submit', async (e) => {
                e.preventDefault();
                try {
                    const data = {
                        codigoLote: document.getElementById('lote-codigo').value,
                        productoId: parseInt(document.getElementById('lote-producto').value),
                        bodegaId: parseInt(document.getElementById('lote-bodega').value),
                        stockInicial: parseInt(document.getElementById('lote-stock').value),
                        fechaFabricacion: document.getElementById('lote-fecha-fab').value || null,
                        fechaVencimiento: document.getElementById('lote-fecha-venc').value || null
                    };
                    const res = await fetch('/api/lotes', {
                        method: 'POST',
                        headers: {
                            'Content-Type': 'application/json',
                            'Authorization': `Bearer ${AuthService.getToken()}`
                        },
                        body: JSON.stringify(data)
                    });
                    if (!res.ok) {
                        const err = await res.json().catch(() => ({}));
                        throw new Error(err.mensaje || 'Error al registrar lote');
                    }
                    Toast.success('Lote registrado exitosamente');
                    App.closeModal('modal-lote');
                    if (typeof LoteController !== 'undefined') LoteController.init();
                    else if (typeof loteController !== 'undefined') loteController.init();
                } catch (err) {
                    Toast.error(err.message || 'Error al registrar lote');
                }
            });
        }

        // Click listeners para abrir modales dinámicos
        document.addEventListener('click', async (e) => {
            if (e.target.closest('#btn-nuevo-ajuste')) {
                const [bodegas, productos] = await Promise.all([
                    BodegaService.getAll().catch(() => []),
                    ProductoService.getAll().catch(() => [])
                ]);
                const selB = document.getElementById('ajuste-bodega');
                const selP = document.getElementById('ajuste-producto');
                if (selB) selB.innerHTML = bodegas.map(b => `<option value="${b.id}">${b.nombre}</option>`).join('');
                if (selP) selP.innerHTML = productos.map(p => `<option value="${p.id}">${p.nombre}</option>`).join('');
                App.openModal('modal-ajuste');
            }

            if (e.target.closest('#btn-nueva-orden-compra')) {
                const [proveedores, bodegas, productos] = await Promise.all([
                    ProveedorService.getAll().catch(() => []),
                    BodegaService.getAll().catch(() => []),
                    ProductoService.getAll().catch(() => [])
                ]);
                const selPr = document.getElementById('oc-proveedor');
                const selB = document.getElementById('oc-bodega');
                const selP = document.getElementById('oc-producto');
                if (selPr) selPr.innerHTML = proveedores.map(pr => `<option value="${pr.id}">${pr.nombre}</option>`).join('');
                if (selB) selB.innerHTML = bodegas.map(b => `<option value="${b.id}">${b.nombre}</option>`).join('');
                if (selP) selP.innerHTML = productos.map(p => `<option value="${p.id}">${p.nombre}</option>`).join('');
                App.openModal('modal-orden-compra');
            }

            if (e.target.closest('#btn-nuevo-lote')) {
                const [bodegas, productos] = await Promise.all([
                    BodegaService.getAll().catch(() => []),
                    ProductoService.getAll().catch(() => [])
                ]);
                const selB = document.getElementById('lote-bodega');
                const selP = document.getElementById('lote-producto');
                if (selB) selB.innerHTML = bodegas.map(b => `<option value="${b.id}">${b.nombre}</option>`).join('');
                if (selP) selP.innerHTML = productos.map(p => `<option value="${p.id}">${p.nombre}</option>`).join('');
                App.openModal('modal-lote');
            }
        });
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
        const isEmpleado = role === 'EMPLEADO';

        // 1. Grupo Administración (Usuarios) -> Solo ADMIN
        document.querySelectorAll('.nav-admin-only, .btn-admin-only, #btn-nuevo-usuario').forEach(el => {
            el.style.display = isAdmin ? '' : 'none';
        });

        // 2. Grupo Control (Auditoría y Reportes) -> ADMIN, SUPERVISOR o GERENTE_LOGISTICA
        document.querySelectorAll('.nav-control-only').forEach(el => {
            el.style.display = (isAdmin || isSupervisor || isGerente) ? '' : 'none';
        });

        // 3. Catálogo Base (+ Nueva Bodega, + Nuevo Producto, + Nueva Categoría) -> Solo ADMIN
        document.querySelectorAll('.btn-manage-only, #btn-nueva-bodega, #btn-nuevo-producto, #btn-nueva-categoria').forEach(el => {
            el.style.display = isAdmin ? '' : 'none';
        });

        // 4. Órdenes de Compra (+ Nueva Orden de Compra) -> ADMIN, SUPERVISOR, JEFE_COMPRAS
        document.querySelectorAll('#btn-nueva-orden-compra').forEach(el => {
            el.style.display = (isAdmin || isSupervisor || isCompras) ? '' : 'none';
        });

        // 5. Pedidos de Clientes (+ Nuevo Pedido) -> ADMIN, SUPERVISOR, EMPLEADO
        document.querySelectorAll('#btn-nuevo-pedido').forEach(el => {
            el.style.display = (isAdmin || isSupervisor || isEmpleado) ? '' : 'none';
        });

        // 6. Ajustes de Inventario / Mermas (+ Registrar Ajuste) -> ADMIN, SUPERVISOR, GERENTE_LOGISTICA
        document.querySelectorAll('#btn-nuevo-ajuste').forEach(el => {
            el.style.display = (isAdmin || isSupervisor || isGerente) ? '' : 'none';
        });

        // 7. Lotes (+ Registrar Lote) -> ADMIN, SUPERVISOR, EMPLEADO
        document.querySelectorAll('#btn-nuevo-lote').forEach(el => {
            el.style.display = (isAdmin || isSupervisor || isEmpleado) ? '' : 'none';
        });

        // 8. Conteos Cíclicos (+ Programar Auditoría) -> ADMIN, SUPERVISOR
        document.querySelectorAll('#btn-nuevo-conteo, #btn-programar-conteo').forEach(el => {
            el.style.display = (isAdmin || isSupervisor) ? '' : 'none';
        });

        // 9. Despachos y Guías (+ Generar Guía, + Transportadora) -> ADMIN, SUPERVISOR, EMPLEADO
        document.querySelectorAll('#btn-nueva-guia, #btn-nueva-transp').forEach(el => {
            el.style.display = (isAdmin || isSupervisor || isEmpleado) ? '' : 'none';
        });

        // 10. Clientes y Proveedores (+ Nuevo Cliente, + Nuevo Proveedor) -> ADMIN, SUPERVISOR, EMPLEADO, GERENTE_LOGISTICA (Oculto para JEFE_COMPRAS)
        const canCreateClientOrProvider = isAdmin || isSupervisor || isEmpleado || isGerente;
        document.querySelectorAll('#btn-nuevo-cliente, #btn-nuevo-proveedor').forEach(el => {
            el.style.display = canCreateClientOrProvider ? '' : 'none';
        });

        // 11. Movimientos Manuales (+ Registrar Movimiento) -> Deshabilitado para GERENTE_LOGISTICA
        const canRegisterMovement = isAdmin || isSupervisor || isCompras || isEmpleado;

        document.querySelectorAll('.btn-op-only, #btn-nuevo-movimiento').forEach(el => {
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
            } else if (view === 'pedidos') {
                if (typeof PedidoController !== 'undefined') await PedidoController.load();
            } else if (view === 'despachos') {
                if (typeof DespachoController !== 'undefined') await DespachoController.load();
            } else if (view === 'picking') {
                if (typeof PickingController !== 'undefined') await PickingController.load();
            } else if (view === 'conteos-ciclicos') {
                if (typeof ConteoController !== 'undefined') await ConteoController.load();
            } else if (view === 'zonas-series') {
                if (typeof ZonasSeriesController !== 'undefined') await ZonasSeriesController.load();
            } else if (view === 'clientes') {
                await ClienteModuleController.load();
            } else if (view === 'proveedores') {
                await ProveedorModuleController.load();
            } else if (view === 'ajustes') {
                if (typeof AjusteController !== 'undefined') await AjusteController.init();
                else if (typeof ajusteController !== 'undefined') await ajusteController.init();
            } else if (view === 'ordenes-compra') {
                if (typeof OrdenCompraController !== 'undefined') await OrdenCompraController.init();
                else if (typeof ordenCompraController !== 'undefined') await ordenCompraController.init();
            } else if (view === 'lotes') {
                if (typeof LoteController !== 'undefined') await LoteController.init();
                else if (typeof loteController !== 'undefined') await loteController.init();
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
            const [bodegasRaw, productosRaw, movimientosRaw] = await Promise.all([
                BodegaService.getAll().catch(() => []),
                ProductoService.getAll().catch(() => []),
                MovimientoService.getAll().catch(() => [])
            ]);

            const bodegas = Array.isArray(bodegasRaw) ? bodegasRaw : (bodegasRaw?.content || []);
            const productos = Array.isArray(productosRaw) ? productosRaw : (productosRaw?.content || []);
            const movimientos = Array.isArray(movimientosRaw) ? movimientosRaw : (movimientosRaw?.content || []);

            const bodegasActivas = bodegas.filter(b => b.activo !== false);

            const totalBodegasEl = document.getElementById('metric-total-bodegas');
            if (totalBodegasEl) totalBodegasEl.innerText = String(bodegasActivas.length || 0).padStart(2, '0');

            const totalMovimientosEl = document.getElementById('metric-total-movimientos');
            const totalMovs = (movimientosRaw && typeof movimientosRaw.totalElements === 'number')
                ? movimientosRaw.totalElements
                : movimientos.length;
            if (totalMovimientosEl) totalMovimientosEl.innerText = totalMovs || 0;

            // Calcular el inventario disponible EXCLUSIVAMENTE en bodegas activas
            const inventariosActivos = await Promise.all(
                bodegasActivas.map(b => BodegaService.getInventario(b.id).catch(() => []))
            );

            let stockTotalActivo = 0;
            inventariosActivos.forEach(invList => {
                const list = Array.isArray(invList) ? invList : (invList?.content || []);
                list.forEach(item => {
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
            
            // Cargar alertas reales de stock de la base de datos
            const alertasPendientes = await AlertaStockService.getPendientes().catch(() => []);
            const totalAlertas = (alertasPendientes && alertasPendientes.length > 0) ? alertasPendientes.length : lowStockCount;
            if (bajoStockMetricEl) bajoStockMetricEl.innerText = String(totalAlertas).padStart(2, '0');

            DashboardRenderer.renderBodegasCards(bodegasActivas);
            DashboardRenderer.renderMovimientosRecientes(movimientos);
            DashboardRenderer.renderBajoStockList(productos.filter(p => p.stock < 10), alertasPendientes);

            if (typeof window.animateGauges === 'function') window.animateGauges();
        } catch (err) {
            console.error('Error cargando dashboard:', err);
        }
    },

    // ===== Centro de Notificaciones & Alertas =====
    initNotifications() {
        this.loadNotificationCount();
        setInterval(() => {
            if (typeof AuthService !== 'undefined' && AuthService.isAuthenticated && AuthService.isAuthenticated()) {
                this.loadNotificationCount();
            }
        }, 20000);

        document.addEventListener('click', (e) => {
            const notifWrapper = document.querySelector('.notification-wrapper');
            const dropdown = document.getElementById('notification-dropdown');
            if (notifWrapper && dropdown && !notifWrapper.contains(e.target)) {
                dropdown.style.display = 'none';
            }
        });
    },

    async loadNotificationCount() {
        if (typeof AuthService === 'undefined' || !AuthService.isAuthenticated || !AuthService.isAuthenticated()) return;
        try {
            const data = await NotificacionService.getContadorNoLeidas().catch(() => null);
            const badge = document.getElementById('notification-badge');
            if (badge && data) {
                const count = data.noLeidas || 0;
                badge.innerText = count > 99 ? '99+' : count;
                badge.style.display = count > 0 ? 'inline-block' : 'none';
            }
        } catch (e) {
            console.error('Error cargando contador de notificaciones:', e);
        }
    },

    async toggleNotificationCenter() {
        const dropdown = document.getElementById('notification-dropdown');
        if (!dropdown) return;
        const isVisible = dropdown.style.display === 'flex';
        if (isVisible) {
            dropdown.style.display = 'none';
        } else {
            dropdown.style.display = 'flex';
            await this.loadNotificationsList();
        }
    },

    async loadNotificationsList() {
        const container = document.getElementById('notification-list-container');
        if (!container) return;
        try {
            container.innerHTML = '<div style="padding: 16px; text-align: center; color: var(--text-muted); font-size: 12px;">Cargando notificaciones...</div>';
            const notifs = await NotificacionService.getAll().catch(() => []);
            if (!notifs || notifs.length === 0) {
                container.innerHTML = '<div style="padding: 16px; text-align: center; color: var(--text-muted); font-size: 12px;">Sin notificaciones recientes</div>';
                return;
            }
            container.innerHTML = notifs.map(n => {
                let icon = 'ℹ️';
                if (n.tipo === 'ALERTA') icon = '[ALERTA]';
                else if (n.tipo === 'EXITO') icon = '[OK]';
                else if (n.tipo === 'ERROR') icon = '[ERROR]';

                const hora = n.fechaCreacion ? new Date(n.fechaCreacion).toLocaleTimeString([], {hour: '2-digit', minute:'2-digit'}) : '';
                return `
                    <div style="padding: 10px 14px; border-bottom: 1px solid rgba(255,255,255,0.05); display: flex; gap: 10px; align-items: flex-start; ${n.leida ? 'opacity: 0.55;' : 'background: rgba(255,255,255,0.03);'}">
                        <span style="font-size: 14px; line-height: 1.2;">${icon}</span>
                        <div style="flex: 1;">
                            <div style="display: flex; justify-content: space-between; align-items: baseline; margin-bottom: 2px;">
                                <strong style="font-size: 12px; color: var(--text-color);">${n.titulo}</strong>
                                <small style="font-size: 10px; color: var(--text-dim);">${hora}</small>
                            </div>
                            <p style="font-size: 11px; color: var(--text-muted); margin: 0; line-height: 1.3;">${n.mensaje}</p>
                            ${!n.leida ? `
                                <div style="display: flex; justify-content: flex-end; margin-top: 4px;">
                                    <button class="btn btn-secondary btn-sm" style="padding: 1px 6px; font-size: 9px;" onclick="App.marcarNotificacionLeida(${n.id}, event)">Marcar leída</button>
                                </div>
                            ` : ''}
                        </div>
                    </div>
                `;
            }).join('');
        } catch (e) {
            container.innerHTML = '<div style="padding: 16px; text-align: center; color: var(--danger); font-size: 12px;">Error al cargar notificaciones</div>';
        }
    },

    async marcarNotificacionLeida(id, event) {
        if (event) event.stopPropagation();
        try {
            await NotificacionService.marcarLeida(id);
            await this.loadNotificationCount();
            await this.loadNotificationsList();
        } catch (e) {
            Toast.error('Error al marcar notificación');
        }
    },

    async marcarTodasNotificacionesLeidas() {
        try {
            await NotificacionService.marcarTodasLeidas();
            await this.loadNotificationCount();
            await this.loadNotificationsList();
            Toast.success('Todas las notificaciones marcadas como leídas');
        } catch (e) {
            Toast.error('Error al actualizar notificaciones');
        }
    },

    async resolverAlertaStock(alertaId) {
        try {
            await AlertaStockService.resolver(alertaId);
            Toast.success('Alerta de stock resuelta');
            this.loadDashboard();
            this.loadNotificationCount();
        } catch (e) {
            Toast.error(e.message || 'Error al resolver alerta');
        }
    },

    async eliminarAlertaStock(alertaId) {
        try {
            await AlertaStockService.eliminar(alertaId);
            Toast.success('Alerta descartada con éxito');
            this.loadDashboard();
            this.loadNotificationCount();
        } catch (e) {
            Toast.error(e.message || 'Error al descartar alerta');
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
        // Garantizar modal único activo: cerrar cualquier otro modal u overlay activo para evitar superposiciones
        if (modalId !== 'modal-confirm' && modalId !== 'modal-prompt') {
            document.querySelectorAll('.modal-overlay.active, .modal-backdrop.active, .drawer-overlay.active, .cmd-overlay.active').forEach(m => {
                if (m.id !== modalId && m.id !== 'modal-confirm' && m.id !== 'modal-prompt') {
                    m.classList.remove('active');
                    m.style.display = 'none';
                }
            });
        }

        const modal = document.getElementById(modalId);
        if (modal) {
            modal.style.display = 'flex';
            modal.classList.add('active');
        }
        if (modalId === 'modal-movimiento' && typeof MovimientoModuleController !== 'undefined') {
            MovimientoModuleController.populateSelects();
        }
    },

    closeModal(modalId) {
        const modal = document.getElementById(modalId);
        if (modal) {
            modal.classList.remove('active');
            modal.style.display = 'none';
        }
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
