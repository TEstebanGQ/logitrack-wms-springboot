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
        return roles.includes(rol);
    },

    isAdmin() {
        return this.getRol() === 'ADMIN';
    },

    isSupervisor() {
        return this.getRol() === 'SUPERVISOR';
    },

    isEmpleado() {
        return this.getRol() === 'EMPLEADO';
    },

    isCompras() {
        return this.getRol() === 'JEFE_COMPRAS';
    },

    isGerente() {
        return this.getRol() === 'GERENTE_LOGISTICA';
    },

    isAuthenticated() {
        return !!ApiService.getToken();
    },

    getToken() {
        return ApiService.getToken();
    }
};

window.AuthService = AuthService;


