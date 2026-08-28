/* ==========================================
   LogiTrack S.A. - Servicio de Productos
   ========================================== */

const ProductoService = {
    getAll() {
        return ApiService.get('/productos');
    },

    getById(id) {
        return ApiService.get(`/productos/${id}`);
    },

    getInventario(id) {
        return ApiService.get(`/productos/${id}/inventario`);
    },

    getBajoStock() {
        return ApiService.get('/productos/stock-bajo');
    },

    create(productoData) {
        return ApiService.post('/productos', productoData);
    },

    update(id, productoData) {
        return ApiService.put(`/productos/${id}`, productoData);
    },

    delete(id) {
        return ApiService.delete(`/productos/${id}`);
    }
};

window.ProductoService = ProductoService;
