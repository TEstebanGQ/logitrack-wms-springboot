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
        const confirmed = typeof ConfirmDialog !== 'undefined' 
            ? await ConfirmDialog.show({
                title: '¿Cerrar Sesión?',
                message: '¿Estás seguro de que deseas salir del sistema?',
                confirmText: 'Sí, Salir',
                cancelText: 'Cancelar',
                type: 'danger'
            })
            : window.confirm('¿Estás seguro de que deseas salir del sistema?');

        if (confirmed) {
            ApiService.removeToken();
            if (window.Router) {
                window.Router.navigate('auth');
            }
        }
    },

    getCurrentUser() {
        const userStr = localStorage.getItem(CONFIG.USER_KEY);
        return userStr ? JSON.parse(userStr) : null;
    },

    isAuthenticated() {
        return !!ApiService.getToken();
    }
};

window.AuthService = AuthService;
