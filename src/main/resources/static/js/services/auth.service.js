/* ==========================================
   LogiTrack S.A. - Servicio de Autenticación
   ========================================== */

const AuthService = {
    async login(email, password) {
        const response = await ApiService.post('/auth/login', { email, password });
        if (response && response.token) {
            ApiService.setToken(response.token);
            localStorage.setItem(CONFIG.USER_KEY, JSON.stringify({
                id: response.id,
                email: response.email,
                nombre: response.nombre,
                rol: response.rol
            }));
        }
        return response;
    },

    async register(nombre, apellido, email, password, rol = 'EMPLEADO') {
        return await ApiService.post('/auth/register', {
            nombre,
            apellido,
            email,
            password,
            rol
        });
    },

    async loginWithGoogle(credential) {
        const response = await ApiService.post('/auth/google', { credential });
        if (response && response.registrado && response.token) {
            ApiService.setToken(response.token);
            localStorage.setItem(CONFIG.USER_KEY, JSON.stringify({
                id: response.id,
                email: response.email,
                nombre: response.nombre,
                rol: response.rol
            }));
        }
        return response;
    },

    async logout() {
        let confirmed = false;
        if (typeof ConfirmDialog !== 'undefined') {
            confirmed = await ConfirmDialog.show({
                title: '¿Cerrar Sesión?',
                message: '¿Estás seguro de que deseas salir del sistema LogiTrack S.A.?',
                confirmText: 'Sí, Salir',
                cancelText: 'Cancelar',
                type: 'danger'
            });
        } else {
            confirmed = window.confirm('¿Estás seguro de que deseas salir del sistema LogiTrack S.A.?');
        }

        if (confirmed) {
            ApiService.removeToken();
            localStorage.clear();
            sessionStorage.clear();
            if (window.App && typeof window.App.closeSidebar === 'function') {
                window.App.closeSidebar();
            }
            window.location.href = window.location.origin + window.location.pathname + '#/auth';
            window.location.reload();
        }
    },

    getCurrentUser() {
        const userStr = localStorage.getItem(CONFIG.USER_KEY);
        return userStr ? JSON.parse(userStr) : null;
    },

    getRol() {
        const u = this.getCurrentUser();
        return u ? u.rol : null;
    },

    hasRole(...roles) {
        const rol = this.getRol();
        if (rol === 'SUPER_ADMIN') return true;
        return roles.includes(rol);
    },

    isSuperAdmin() {
        return this.getRol() === 'SUPER_ADMIN';
    },

    isAdmin() {
        const rol = this.getRol();
        return rol === 'ADMIN' || rol === 'SUPER_ADMIN';
    },

    isSupervisor() {
        const rol = this.getRol();
        return rol === 'SUPERVISOR' || rol === 'SUPER_ADMIN';
    },

    isEmpleado() {
        const rol = this.getRol();
        return rol === 'EMPLEADO' || rol === 'SUPER_ADMIN';
    },

    isCompras() {
        const rol = this.getRol();
        return rol === 'JEFE_COMPRAS' || rol === 'SUPER_ADMIN';
    },

    isGerente() {
        const rol = this.getRol();
        return rol === 'GERENTE_LOGISTICA' || rol === 'SUPER_ADMIN';
    },

    isAuthenticated() {
        return !!ApiService.getToken();
    },

    getToken() {
        return ApiService.getToken();
    }
};

window.AuthService = AuthService;


