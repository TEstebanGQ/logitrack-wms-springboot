/* ==========================================
   LogiTrack S.A. - Main Application Controller
   ========================================== */

const App = {
    init() {
        console.log('LogiTrack Frontend Inicializado...');
        this.bindEvents();
        Router.init();
        this.updateUserInfo();
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
                    Toast.error(err.message || 'Error de autenticación');
                }
            });
        }

        // Formulario Registro
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
                    Toast.success('Usuario registrado con éxito. Por favor inicia sesión.');
                    this.toggleAuthMode('login');
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
                const id = document.getElementById('bodega-id').value;
                const data = {
                    nombre: document.getElementById('bodega-nombre').value,
                    ubicacion: document.getElementById('bodega-ubicacion').value,
                    capacidad: parseInt(document.getElementById('bodega-capacidad').value),
                    encargado: document.getElementById('bodega-encargado').value,
                    activo: document.getElementById('bodega-activo').checked
                };

                try {
                    if (id) {
                        await BodegaService.update(id, data);
                        Toast.success('Bodega actualizada correctamente');
                    } else {
                        await BodegaService.create(data);
                        Toast.success('Bodega creada correctamente');
                    }
                    this.closeModal('modal-bodega');
                    this.loadViewData('bodegas');
                } catch (err) {
                    Toast.error(err.message);
                }
            });
        }

        // Formulario Producto Modal
        const productoForm = document.getElementById('form-producto');
        if (productoForm) {
            productoForm.addEventListener('submit', async (e) => {
                e.preventDefault();
                const id = document.getElementById('producto-id').value;
                const catIdVal = document.getElementById('producto-categoria-id').value;
                const data = {
                    nombre: document.getElementById('producto-nombre').value,
                    categoriaId: catIdVal ? parseInt(catIdVal) : null,
                    stock: parseInt(document.getElementById('producto-stock').value),
                    precio: parseFloat(document.getElementById('producto-precio').value),
                    descripcion: document.getElementById('producto-descripcion').value
                };

                try {
                    if (id) {
                        await ProductoService.update(id, data);
                        Toast.success('Producto actualizado correctamente');
                    } else {
                        await ProductoService.create(data);
                        Toast.success('Producto creado correctamente');
                    }
                    this.closeModal('modal-producto');
                    this.loadViewData('productos');
                } catch (err) {
                    Toast.error(err.message);
                }
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

                try {
                    await MovimientoService.registrar(data);
                    Toast.success('Movimiento de inventario registrado con éxito');
                    this.closeModal('modal-movimiento');
                    this.loadViewData('movimientos');
                } catch (err) {
                    Toast.error(err.message);
                }
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
    },

    async loadViewData(view) {
        try {
            if (view === 'dashboard') {
                this.loadDashboardData();
            } else if (view === 'reportes') {
                const reporte = await ReporteService.getResumenGeneral().catch(() => null);
                Renderers.renderReportesSection(reporte, 'reportes-list-container');
                this.initExportButtons();
            } else if (view === 'bodegas') {
                const bodegas = await BodegaService.getAll();
                Renderers.renderBodegasTable(bodegas, 'bodegas-list-container');
            } else if (view === 'productos') {
                const productos = await ProductoService.getAll();
                Renderers.renderProductosTable(productos, 'productos-list-container');
            } else if (view === 'movimientos') {
                const movimientos = await MovimientoService.getAll();
                Renderers.renderMovimientosTable(movimientos, 'movimientos-list-container');
            } else if (view === 'auditorias') {
                const auditorias = await AuditoriaService.getAll();
                Renderers.renderAuditoriasTable(auditorias, 'auditorias-list-container');
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

            // Filtrar únicamente bodegas activas para las métricas y tarjetas
            const bodegasActivas = bodegas.filter(b => b.activo !== false);

            const totalBodegasEl = document.getElementById('metric-total-bodegas');
            if (totalBodegasEl) totalBodegasEl.innerText = String(bodegasActivas.length || 0).padStart(2, '0');

            const totalMovimientosEl = document.getElementById('metric-total-movimientos');
            if (totalMovimientosEl) totalMovimientosEl.innerText = movimientos.length || 0;

            // Stock total global
            const stockTotal = productos.reduce((sum, p) => sum + (p.stock || 0), 0);
            const stockEl = document.getElementById('metric-stock-total');
            if (stockEl) stockEl.innerText = stockTotal.toLocaleString('es-CO');

            const lowStockCount = productos.filter(p => p.stock < 10).length;
            const bajoStockMetricEl = document.getElementById('metric-bajo-stock');
            if (bajoStockMetricEl) bajoStockMetricEl.innerText = String(lowStockCount).padStart(2, '0');

            // Renderizar únicamente tarjetas de bodegas activas
            Renderers.renderDashboardBodegasCards(bodegasActivas, 'dashboard-bodegas-container');

            // Renderizar movimientos recientes (tabla compacta)
            Renderers.renderDashboardMovimientosRecientes(movimientos, 'dashboard-movimientos-container');

            // Renderizar lista lateral de stock bajo
            const bajoStockProductos = productos.filter(p => p.stock < 10);
            Renderers.renderDashboardBajoStockList(bajoStockProductos, 'dashboard-bajo-stock-container');

            // Animar gauges si existen en el DOM
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

    initExportButtons() {
        if (typeof ExportService === 'undefined') return;
        const map = {
            'btn-export-excel-resumen': () => ExportService.exportarResumenExcel(),
            'btn-export-pdf-resumen':   () => ExportService.exportarResumenPdf(),
            'btn-export-excel-mov':     () => ExportService.exportarMovimientosExcel(),
            'btn-export-pdf-mov':       () => ExportService.exportarMovimientosPdf(),
        };
        Object.entries(map).forEach(([id, fn]) => {
            const btn = document.getElementById(id);
            if (btn) {
                // Prevenir múltiples listeners si se llama repetidamente
                btn.removeEventListener('click', fn);
                btn.addEventListener('click', fn);
            }
        });
    },

    inspectAuditoria(id) {
        const store = window.auditoriaDataStore || [];
        const item = store.find(a => a.id === id);
        if (!item) return;

        const prevEl = document.getElementById('aud-json-anteriores');
        const newEl = document.getElementById('aud-json-nuevos');
        const titleEl = document.getElementById('aud-modal-title');

        if (titleEl) titleEl.innerText = `Auditoría #${item.id} - ${item.entidad} (${item.tipoOperacion})`;

        try {
            prevEl.innerText = item.valoresAnteriores ? JSON.stringify(JSON.parse(item.valoresAnteriores), null, 2) : '(Sin valores previos)';
        } catch(e) {
            prevEl.innerText = item.valoresAnteriores || '(Sin valores previos)';
        }

        try {
            newEl.innerText = item.valoresNuevos ? JSON.stringify(JSON.parse(item.valoresNuevos), null, 2) : '(Sin valores nuevos)';
        } catch(e) {
            newEl.innerText = item.valoresNuevos || '(Sin valores nuevos)';
        }

        this.openModal('modal-auditoria');
    },

    // Modal Control Functions
    openModal(modalId) {
        const modal = document.getElementById(modalId);
        if (modal) modal.classList.add('active');
        if (modalId === 'modal-movimiento') {
            this.populateMovimientoSelects();
        }
    },

    closeModal(modalId) {
        const modal = document.getElementById(modalId);
        if (modal) modal.classList.remove('active');
    },

    openBodegaModal() {
        document.getElementById('form-bodega').reset();
        document.getElementById('bodega-id').value = '';
        document.getElementById('modal-bodega-title').innerText = 'Nueva Bodega';
        this.openModal('modal-bodega');
    },

    async editBodega(id) {
        try {
            const bodega = await BodegaService.getById(id);
            document.getElementById('bodega-id').value = bodega.id;
            document.getElementById('bodega-nombre').value = bodega.nombre;
            document.getElementById('bodega-ubicacion').value = bodega.ubicacion;
            document.getElementById('bodega-capacidad').value = bodega.capacidad;
            document.getElementById('bodega-encargado').value = bodega.encargado;
            document.getElementById('bodega-activo').checked = bodega.activo;
            document.getElementById('modal-bodega-title').innerText = 'Editar Bodega';
            this.openModal('modal-bodega');
        } catch (err) {
            Toast.error('No se pudo cargar la bodega');
        }
    },

    async deleteBodega(id) {
        if (!confirm('¿Estás seguro de desactivar/eliminar esta bodega?')) return;
        try {
            await BodegaService.delete(id);
            Toast.success('Bodega eliminada correctamente');
            this.loadViewData('bodegas');
        } catch (err) {
            Toast.error(err.message);
        }
    },

    async openProductoModal() {
        document.getElementById('form-producto').reset();
        document.getElementById('producto-id').value = '';
        document.getElementById('modal-producto-title').innerText = 'Nuevo Producto';
        await this.populateCategoriaSelect();
        this.openModal('modal-producto');
    },

    async editProducto(id) {
        try {
            const producto = await ProductoService.getById(id);
            await this.populateCategoriaSelect(producto.categoriaId);
            document.getElementById('producto-id').value = producto.id;
            document.getElementById('producto-nombre').value = producto.nombre;
            if (document.getElementById('producto-categoria-id')) {
                document.getElementById('producto-categoria-id').value = producto.categoriaId || '';
            }
            document.getElementById('producto-stock').value = producto.stock;
            document.getElementById('producto-precio').value = producto.precio;
            document.getElementById('producto-descripcion').value = producto.descripcion || '';
            document.getElementById('modal-producto-title').innerText = 'Editar Producto';
            this.openModal('modal-producto');
        } catch (err) {
            Toast.error('No se pudo cargar el producto');
        }
    },

    async populateCategoriaSelect(selectedId = null) {
        try {
            const categorias = await CategoriaService.getAll().catch(() => []);
            const catSelect = document.getElementById('producto-categoria-id');
            if (catSelect) {
                catSelect.innerHTML = categorias.map(c =>
                    `<option value="${c.id}" ${c.id === selectedId ? 'selected' : ''}>${c.nombre}</option>`
                ).join('');
            }
        } catch (err) {
            console.error('Error cargando categorías:', err);
        }
    },

    async deleteProducto(id) {
        if (!confirm('¿Estás seguro de desactivar/eliminar este producto?')) return;
        try {
            await ProductoService.delete(id);
            Toast.success('Producto eliminado correctamente');
            this.loadViewData('productos');
        } catch (err) {
            Toast.error(err.message);
        }
    },

    async populateMovimientoSelects() {
        try {
            const [bodegas, productos] = await Promise.all([
                BodegaService.getAll(true),
                ProductoService.getAll()
            ]);

            const origenSelect = document.getElementById('mov-origen');
            const destinoSelect = document.getElementById('mov-destino');
            const prodSelect = document.getElementById('mov-producto');

            const bodegaOpts = bodegas.map(b => `<option value="${b.id}">${b.nombre} (${b.ubicacion})</option>`).join('');
            const prodOpts = productos.map(p => `<option value="${p.id}">${p.nombre} (Stock: ${p.stock})</option>`).join('');

            origenSelect.innerHTML = `<option value="">-- Ninguna --</option>` + bodegaOpts;
            destinoSelect.innerHTML = `<option value="">-- Ninguna --</option>` + bodegaOpts;
            prodSelect.innerHTML = prodOpts;
        } catch (err) {
            console.error('Error cargando selects para movimiento:', err);
        }
    }
};

document.addEventListener('DOMContentLoaded', () => {
    App.init();
});

window.App = App;
