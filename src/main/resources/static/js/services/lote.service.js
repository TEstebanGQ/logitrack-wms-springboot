/* ==========================================
   LogiTrack S.A. - Servicio de Lotes y Vencimientos (FEFO)
   ========================================== */

const LoteService = {
    getAll() {
        return ApiService.get('/lotes');
    },

    getById(id) {
        return ApiService.get(`/lotes/${id}`);
    },

    getByProducto(productoId) {
        return ApiService.get(`/lotes/producto/${productoId}`);
    },

    getByBodega(bodegaId) {
        return ApiService.get(`/lotes/bodega/${bodegaId}`);
    },

    getFEFO(productoId, bodegaId) {
        return ApiService.get(`/lotes/fefo?productoId=${productoId}&bodegaId=${bodegaId}`);
    },

    getProximosVencer(dias = 30) {
        return ApiService.get(`/lotes/proximos-vencer?dias=${dias}`);
    },

    create(datos) {
        return ApiService.post('/lotes', datos);
    },

    updateEstado(id, estado) {
        return ApiService.put(`/lotes/${id}/estado?estado=${estado}`, {});
    }
};

window.LoteService = LoteService;
