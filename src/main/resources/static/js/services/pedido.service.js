/* ==========================================
   LogiTrack S.A. - Pedido Service
   ========================================== */

const PedidoService = {
    getAll() {
        return ApiService.get('/pedidos');
    },

    getById(id) {
        return ApiService.get(`/pedidos/${id}`);
    },

    getByCliente(clienteId) {
        return ApiService.get(`/pedidos/cliente/${clienteId}`);
    },

    create(data) {
        return ApiService.post('/pedidos', data);
    },

    cambiarEstado(id, nuevoEstado, observaciones) {
        let url = `/pedidos/${id}/estado?nuevoEstado=${encodeURIComponent(nuevoEstado)}`;
        if (observaciones) url += `&observaciones=${encodeURIComponent(observaciones)}`;
        return ApiService.patch(url, {});
    },

    despachar(id) {
        return ApiService.post(`/pedidos/${id}/despachar`, {});
    },

    cancelar(id, motivo) {
        let url = `/pedidos/${id}/cancelar`;
        if (motivo) url += `?motivo=${encodeURIComponent(motivo)}`;
        return ApiService.post(url, {});
    }
};

window.PedidoService = PedidoService;
