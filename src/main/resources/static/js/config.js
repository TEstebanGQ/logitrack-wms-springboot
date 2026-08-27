/* ==========================================
   LogiTrack S.A. - Config global
   ========================================== */

const CONFIG = {
    API_BASE_URL: (typeof window !== 'undefined' && window.location.origin.startsWith('http')) 
        ? `${window.location.origin}/api` 
        : 'http://localhost:8081/api',
    TOKEN_KEY: 'logitrack_jwt_token',
    USER_KEY: 'logitrack_user_data'
};

if (typeof window !== 'undefined') {
    window.CONFIG = CONFIG;
}
