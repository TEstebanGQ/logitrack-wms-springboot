/* ==========================================
   LogiTrack S.A. - Despacho & Guías Service
   ========================================== */

const DespachoService = {
    getGuias() {
        return ApiService.get('/guias-despacho');
    },

    getGuiaById(id) {
        return ApiService.get(`/guias-despacho/${id}`);
    },

    getGuiaByPedido(pedidoId) {
        return ApiService.get(`/guias-despacho/pedido/${pedidoId}`);
    },

    crearGuia(data) {
        return ApiService.post('/guias-despacho', data);
    },

    actualizarEstadoGuia(id, nuevoEstado, observaciones) {
        let url = `/guias-despacho/${id}/estado?nuevoEstado=${encodeURIComponent(nuevoEstado)}`;
        if (observaciones) url += `&observaciones=${encodeURIComponent(observaciones)}`;
        return ApiService.patch(url, {});
    },

    getTransportadoras() {
        return ApiService.get('/transportadoras');
    },

    crearTransportadora(data) {
        return ApiService.post('/transportadoras', data);
    }
};

window.DespachoService = DespachoService;
