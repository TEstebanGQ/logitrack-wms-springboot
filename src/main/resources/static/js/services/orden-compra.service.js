/* ==========================================
   LogiTrack S.A. - Servicio de Órdenes de Compra
   ========================================== */

const OrdenCompraService = {
    getAll() {
        return ApiService.get('/ordenes-compra');
    },

    getById(id) {
        return ApiService.get(`/ordenes-compra/${id}`);
    },

    getByProveedor(proveedorId) {
        return ApiService.get(`/ordenes-compra/proveedor/${proveedorId}`);
    },

    getByEstado(estado) {
        return ApiService.get(`/ordenes-compra/estado/${estado}`);
    },

    create(datos) {
        return ApiService.post('/ordenes-compra', datos);
    },

    aprobar(id) {
        return ApiService.put(`/ordenes-compra/${id}/aprobar`, {});
    },

    cancelar(id, motivo) {
        return ApiService.put(`/ordenes-compra/${id}/cancelar`, { motivo });
    },

    recibir(id) {
        return ApiService.put(`/ordenes-compra/${id}/recibir`, {});
    }
};

window.OrdenCompraService = OrdenCompraService;
