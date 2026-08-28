/* ==========================================
   LogiTrack S.A. - Tareas de Picking Service
   ========================================== */

const PickingService = {
    getAll() {
        return ApiService.get('/picking');
    },

    getById(id) {
        return ApiService.get(`/picking/${id}`);
    },

    getByPedido(pedidoId) {
        return ApiService.get(`/picking/pedido/${pedidoId}`);
    },

    recolectar(id, cantidadRecogida, notas) {
        let url = `/picking/${id}/recolectar?cantidadRecogida=${cantidadRecogida}`;
        if (notas) url += `&notas=${encodeURIComponent(notas)}`;
        return ApiService.patch(url, {});
    },

    cambiarEstado(id, nuevoEstado) {
        return ApiService.patch(`/picking/${id}/estado?nuevoEstado=${encodeURIComponent(nuevoEstado)}`, {});
    }
};

window.PickingService = PickingService;
