/* ==========================================
   LogiTrack S.A. - Servicio de Reportes
   ========================================== */

const ReporteService = {
    getResumenGeneral() {
        return ApiService.get('/reportes/resumen');
    },

    getMovimientosFiltrados(params = {}) {
        const q = new URLSearchParams();
        if (params.bodega) q.append('bodega', params.bodega);
        if (params.producto) q.append('producto', params.producto);
        if (params.tipoMovimiento) q.append('tipoMovimiento', params.tipoMovimiento);
        if (params.fechaInicio) q.append('fechaInicio', params.fechaInicio);
        if (params.fechaFin) q.append('fechaFin', params.fechaFin);
        const queryStr = q.toString() ? `?${q.toString()}` : '';
        return ApiService.get(`/reportes/movimientos${queryStr}`);
    },

    getAuditoriaFiltrada(params = {}) {
        const q = new URLSearchParams();
        if (params.producto) q.append('producto', params.producto);
        if (params.fechaInicio) q.append('fechaInicio', params.fechaInicio);
        if (params.fechaFin) q.append('fechaFin', params.fechaFin);
        if (params.campoModificado) q.append('campoModificado', params.campoModificado);
        const queryStr = q.toString() ? `?${q.toString()}` : '';
        return ApiService.get(`/reportes/auditoria${queryStr}`);
    },

    getClasificacionAbc() {
        return ApiService.get('/reportes/clasificacion-abc');
    }
};

window.ReporteService = ReporteService;
