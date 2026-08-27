/* ==========================================
   LogiTrack S.A. - Servicio de Movimientos
   ========================================== */

const MovimientoService = {
    getAll() {
        return ApiService.get('/movimientos');
    },

    getById(id) {
        return ApiService.get(`/movimientos/${id}`);
    },

    registrar(movimientoData) {
        return ApiService.post('/movimientos', movimientoData);
    },

    filtrar(fechaInicio, fechaFin, tipo) {
        let query = [];
        if (fechaInicio) query.push(`fechaInicio=${fechaInicio}`);
        if (fechaFin) query.push(`fechaFin=${fechaFin}`);
        if (tipo) query.push(`tipo=${tipo}`);
        const queryString = query.length ? `?${query.join('&')}` : '';
        return ApiService.get(`/movimientos${queryString}`);
    }
};

window.MovimientoService = MovimientoService;
