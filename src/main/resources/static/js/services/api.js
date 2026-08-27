/* ==========================================
   LogiTrack S.A. - Cliente API Base
   ========================================== */

const ApiService = {
    getToken() {
        return localStorage.getItem(CONFIG.TOKEN_KEY);
    },

    setToken(token) {
        localStorage.setItem(CONFIG.TOKEN_KEY, token);
    },

    removeToken() {
        localStorage.removeItem(CONFIG.TOKEN_KEY);
        localStorage.removeItem(CONFIG.USER_KEY);
    },

    async request(endpoint, options = {}) {
        const token = this.getToken();
        const headers = {
            'Content-Type': 'application/json',
            ...(options.headers || {})
        };

        if (token) {
            headers['Authorization'] = `Bearer ${token}`;
        }

        const config = {
            ...options,
            headers
        };

        try {
            const response = await fetch(`${CONFIG.API_BASE_URL}${endpoint}`, config);

            let data;
            const contentType = response.headers.get('content-type');
            if (contentType && contentType.includes('application/json')) {
                data = await response.json().catch(() => ({}));
            } else {
                const text = await response.text().catch(() => '');
                data = { mensaje: text || response.statusText };
            }

            if (!response.ok) {
                if (response.status === 401) {
                    if (endpoint.includes('/auth/')) {
                        const msg = data.mensaje || data.message || 'Correo o contraseña incorrectos';
                        throw new Error(msg);
                    } else {
                        this.removeToken();
                        if (window.Router) {
                            window.Router.navigate('auth');
                        }
                        throw new Error('Sesión expirada o no autorizada');
                    }
                }
                const message = data.mensaje || data.message || 'Error en la solicitud';
                throw new Error(message);
            }

            return data;
        } catch (error) {
            console.error(`API Error [${endpoint}]:`, error);
            throw error;
        }
    },

    get(endpoint) {
        return this.request(endpoint, { method: 'GET' });
    },

    post(endpoint, body) {
        return this.request(endpoint, { method: 'POST', body: JSON.stringify(body) });
    },

    put(endpoint, body) {
        return this.request(endpoint, { method: 'PUT', body: JSON.stringify(body) });
    },

    delete(endpoint) {
        return this.request(endpoint, { method: 'DELETE' });
    }
};

window.ApiService = ApiService;
