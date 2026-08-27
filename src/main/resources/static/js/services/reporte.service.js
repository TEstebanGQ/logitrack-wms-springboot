/* ==========================================
   LogiTrack S.A. - Servicio de Reportes
   ========================================== */

const ReporteService = {
    getResumenGeneral() {
        return ApiService.get('/reportes/resumen');
    }
};

window.ReporteService = ReporteService;
