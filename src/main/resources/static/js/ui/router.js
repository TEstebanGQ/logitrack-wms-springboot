/* ==========================================
   LogiTrack S.A. - UI Router SPA (Modular Template Loader)
   ========================================== */

const Router = {
    currentView: 'auth',

    init() {
        window.addEventListener('hashchange', () => this.handleHashChange());
        this.handleHashChange();
    },

    handleHashChange() {
        const hash = window.location.hash.replace('#/', '') || 'dashboard';

        if (!AuthService.isAuthenticated() && hash !== 'auth') {
            this.navigate('auth');
            return;
        }

        if (AuthService.isAuthenticated() && hash === 'auth') {
            this.navigate('dashboard');
            return;
        }

        this.showView(hash);
    },

    navigate(view) {
        window.location.hash = `#/${view}`;
    },

    async showView(viewId) {
        this.currentView = viewId;
        const appLayout  = document.getElementById('app-layout');
        const authLayout = document.getElementById('auth-layout');

        if (viewId === 'auth') {
            if (appLayout)  appLayout.style.display  = 'none';
            if (authLayout) authLayout.style.display = 'grid';
            return;
        }

        const user = AuthService.getCurrentUser();
        const role = user ? user.rol : 'EMPLEADO';

        if (viewId === 'usuarios') {
            if (role !== 'ADMIN') {
                Toast.error('Acceso denegado: Se requiere rol de Administrador');
                this.navigate('dashboard');
                return;
            }
        }

        if (viewId === 'auditorias' || viewId === 'reportes') {
            if (role !== 'ADMIN' && role !== 'SUPERVISOR' && role !== 'GERENTE_LOGISTICA') {
                Toast.error('Acceso denegado: Se requiere rol de Administrador, Supervisor o Gerente');
                this.navigate('dashboard');
                return;
            }
        }

        // Mostrar dashboard layout
        if (appLayout)  appLayout.style.display  = 'flex';
        if (authLayout) authLayout.style.display = 'none';

        // Cargar vista modular desde /views/${viewId}.html si no está en el DOM
        const contentArea = document.querySelector('.content-area');
        let targetView = document.getElementById(`view-${viewId}`);
        if (!targetView && contentArea) {
            try {
                const res = await fetch(`views/${viewId}.html`);
                if (res.ok) {
                    const htmlText = await res.text();
                    const doc = new DOMParser().parseFromString(htmlText, 'text/html');
                    let extractedSection = doc.querySelector('.view-section');
                    if (!extractedSection) {
                        extractedSection = document.createElement('section');
                        extractedSection.id = `view-${viewId}`;
                        extractedSection.className = 'view-section';
                        extractedSection.innerHTML = doc.body.innerHTML;
                    }
                    targetView = extractedSection;
                    contentArea.appendChild(targetView);
                }
            } catch (e) {
                console.error(`Error al cargar la plantilla views/${viewId}.html:`, e);
            }
        }

        // Sincronizar visibilidad de menú del sidebar y botones según rol
        if (window.App && typeof window.App.updateUserInfo === 'function') {
            window.App.updateUserInfo();
        }

        // Ocultar todas las vistas y mostrar la activa
        document.querySelectorAll('.view-section').forEach(v => v.style.display = 'none');
        if (targetView) targetView.style.display = 'block';

        // Actualizar estado activo en navegación
        document.querySelectorAll('.nav-link').forEach(link => {
            link.classList.toggle('active', link.getAttribute('data-view') === viewId);
        });

        // Cerrar menú hamburguesa móvil y drawer lateral si están abiertos
        if (window.App && typeof window.App.closeSidebar === 'function') {
            window.App.closeSidebar();
        }
        if (window.App && typeof window.App.closeDrawer === 'function') {
            window.App.closeDrawer();
        }

        // Actualizar título topbar
        const pageTitle = document.getElementById('page-title');
        if (pageTitle) {
            const titles = {
                dashboard:  'Panel de Bodegas',
                bodegas:    'Gestión de Bodegas',
                productos:  'Gestión de Productos',
                movimientos:'Movimientos de Inventario',
                pedidos:    'Pedidos de Clientes y Ventas',
                despachos:  'Despachos y Guías de Envío',
                picking:    'Monitor de Picking y Recolección',
                'conteos-ciclicos': 'Conteos Cíclicos y Auditoría Física',
                'zonas-series': 'Estructura Física, Series y Unidades',
                clientes:   'Directorio de Clientes',
                proveedores:'Directorio de Proveedores',
                'ordenes-compra': 'Órdenes de Compra a Proveedores',
                ajustes:    'Ajustes y Control de Mermas',
                lotes:      'Lotes y Vencimientos (FIFO/FEFO)',
                auditorias: 'Registros de Auditoría',
                reportes:   'Reportes y Métricas',
                usuarios:   'Gestión de Usuarios (Solo Admin)'
            };
            pageTitle.innerText = titles[viewId] || 'LogiTrack S.A.';
        }

        // Cargar datos de la vista
        if (window.App && typeof window.App.loadViewData === 'function') {
            window.App.loadViewData(viewId);
        }
    }
};

window.Router = Router;
