/* ==========================================
   LogiTrack S.A. - Servicio de Autenticación
   ========================================== */

const AuthService = {
    async login(email, password) {
        const response = await ApiService.post('/auth/login', { email, password });
        if (response && response.token) {
            ApiService.setToken(response.token);
            localStorage.setItem(CONFIG.USER_KEY, JSON.stringify({
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

    logout() {
        ApiService.removeToken();
        if (window.Router) {
            window.Router.navigate('auth');
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
