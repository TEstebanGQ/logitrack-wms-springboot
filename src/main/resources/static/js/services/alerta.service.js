/* ==========================================
   LogiTrack S.A. - Alertas de Stock Service
   ========================================== */

const AlertaStockService = {
    async getAll(soloPendientes = false) {
        return ApiService.get(`/alertas?soloPendientes=${soloPendientes}`);
    },

    async getPendientes() {
        return ApiService.get('/alertas/pendientes');
    },

    async getPorProducto(productoId) {
        return ApiService.get(`/alertas/producto/${productoId}`);
    },

    async resolver(alertaId) {
        return ApiService.put(`/alertas/${alertaId}/resolver`, {});
    },

    async eliminar(alertaId) {
        return ApiService.delete(`/alertas/${alertaId}`);
    }
};


window.AlertaStockService = AlertaStockService;
