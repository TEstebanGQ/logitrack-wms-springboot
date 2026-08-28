/* ==========================================
   LogiTrack S.A. - Clientes Service
   ========================================== */

const ClienteService = {
    async getAll(soloActivos = true) {
        return ApiService.get(`/clientes?soloActivos=${soloActivos}`);
    },

    async getById(id) {
        return ApiService.get(`/clientes/${id}`);
    },

    async create(data) {
        return ApiService.post('/clientes', data);
    },

    async update(id, data) {
        return ApiService.put(`/clientes/${id}`, data);
    },

    async delete(id) {
        return ApiService.delete(`/clientes/${id}`);
    },

    async getMovimientos(id) {
        return ApiService.get(`/clientes/${id}/movimientos`);
    }
};

window.ClienteService = ClienteService;
