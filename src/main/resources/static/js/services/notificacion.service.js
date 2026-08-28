/* ==========================================
   LogiTrack S.A. - Notificaciones Service
   ========================================== */

const NotificacionService = {
    async getAll() {
        return ApiService.get('/notificaciones');
    },

    async getContadorNoLeidas() {
        return ApiService.get('/notificaciones/no-leidas/contador');
    },

    async marcarLeida(id) {
        return ApiService.put(`/notificaciones/${id}/leer`, {});
    },

    async marcarTodasLeidas() {
        return ApiService.put('/notificaciones/leer-todas', {});
    }
};

window.NotificacionService = NotificacionService;
