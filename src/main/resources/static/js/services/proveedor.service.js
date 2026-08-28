/* ==========================================
   LogiTrack S.A. - Proveedores Service
   ========================================== */

const ProveedorService = {
    async getAll(soloActivos = true) {
        return ApiService.get(`/proveedores?soloActivos=${soloActivos}`);
    },

    async getById(id) {
        return ApiService.get(`/proveedores/${id}`);
    },

    async create(data) {
        return ApiService.post('/proveedores', data);
    },

    async update(id, data) {
        return ApiService.put(`/proveedores/${id}`, data);
    },

    async delete(id) {
        return ApiService.delete(`/proveedores/${id}`);
    },

    async getMovimientos(id) {
        return ApiService.get(`/proveedores/${id}/movimientos`);
    }
};

window.ProveedorService = ProveedorService;
