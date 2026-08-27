/* ==========================================
   LogiTrack S.A. - Main Application Orchestrator
   ========================================== */

const App = {
    init() {
        console.log('LogiTrack S.A. Frontend Orquestado Inicializado...');
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
                const data = {
                    nombre: document.getElementById('producto-nombre').value,
                    categoriaId: catIdVal ? parseInt(catIdVal) : null,
                    stock: parseInt(document.getElementById('producto-stock').value),
                    precio: parseFloat(document.getElementById('producto-precio').value),
                    descripcion: document.getElementById('producto-descripcion').value
                };
                await ProductoModuleController.save(data);
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
                await ReporteModuleController.load();
            } else if (view === 'bodegas') {
                await BodegaModuleController.load();
            } else if (view === 'productos') {
                await ProductoModuleController.load();
            } else if (view === 'movimientos') {
                await MovimientoModuleController.load();
            } else if (view === 'auditorias') {
                await AuditoriaModuleController.load();
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

            const stockTotal = productos.reduce((sum, p) => sum + (p.stock || 0), 0);
            const stockEl = document.getElementById('metric-stock-total');
            if (stockEl) stockEl.innerText = stockTotal.toLocaleString('es-CO');

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
