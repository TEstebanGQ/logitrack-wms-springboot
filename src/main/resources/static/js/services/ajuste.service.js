/* ==========================================
   LogiTrack S.A. - Servicio de Ajustes de Inventario
   ========================================== */

const AjusteService = {
    getAll() {
        return ApiService.get('/ajustes');
    },

    getById(id) {
        return ApiService.get(`/ajustes/${id}`);
    },

    getByBodega(bodegaId) {
        return ApiService.get(`/ajustes/bodega/${bodegaId}`);
    },

    getByProducto(productoId) {
        return ApiService.get(`/ajustes/producto/${productoId}`);
    },

    getByTipo(tipo) {
        return ApiService.get(`/ajustes/tipo/${tipo}`);
    },

    create(datos) {
        return ApiService.post('/ajustes', datos);
    }
};

window.AjusteService = AjusteService;
