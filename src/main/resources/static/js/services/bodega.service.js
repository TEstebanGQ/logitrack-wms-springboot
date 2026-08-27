/* ==========================================
   LogiTrack S.A. - Servicio de Bodegas
   ========================================== */

const BodegaService = {
    getAll(soloActivas = false) {
        return ApiService.get(soloActivas ? '/bodegas?soloActivas=true' : '/bodegas');
    },

    getById(id) {
        return ApiService.get(`/bodegas/${id}`);
    },

    getInventario(id) {
        return ApiService.get(`/bodegas/${id}/inventario`);
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
