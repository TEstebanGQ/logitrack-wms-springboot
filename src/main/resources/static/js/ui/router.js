/* ==========================================
   LogiTrack S.A. - UI Router SPA
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

    showView(viewId) {
        this.currentView = viewId;
        const appLayout  = document.getElementById('app-layout');
        const authLayout = document.getElementById('auth-layout');

        if (viewId === 'auth') {
            if (appLayout)  appLayout.style.display  = 'none';
            if (authLayout) authLayout.style.display = 'grid';
            return;
        }

        // Mostrar dashboard layout
        if (appLayout)  appLayout.style.display  = 'flex';
        if (authLayout) authLayout.style.display = 'none';

        // Ocultar todas las vistas y mostrar la activa
        document.querySelectorAll('.view-section').forEach(v => v.style.display = 'none');
        const targetView = document.getElementById(`view-${viewId}`);
        if (targetView) targetView.style.display = 'block';

        // Actualizar estado activo en navegación
        document.querySelectorAll('.nav-link').forEach(link => {
            link.classList.toggle('active', link.getAttribute('data-view') === viewId);
        });

        // Actualizar título topbar
        const pageTitle = document.getElementById('page-title');
        if (pageTitle) {
            const titles = {
                dashboard:  'Panel de Bodegas',
                bodegas:    'Gestión de Bodegas',
                productos:  'Gestión de Productos',
                movimientos:'Movimientos de Inventario',
                auditorias: 'Registros de Auditoría',
                reportes:   'Reportes y Métricas'
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
