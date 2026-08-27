/* ==========================================
   LogiTrack S.A. - Servicio de Bodegas
   ========================================== */

const BodegaService = {
    getAll() {
        return ApiService.get('/bodegas');
    },

    getById(id) {
        return ApiService.get(`/bodegas/${id}`);
    },

    create(bodegaData) {
        return ApiService.post('/bodegas', bodegaData);
    },

    update(id, bodegaData) {
        return ApiService.put(`/bodegas/${id}`, bodegaData);
    },

    delete(id) {
        return ApiService.delete(`/bodegas/${id}`);
    }
};

window.BodegaService = BodegaService;
