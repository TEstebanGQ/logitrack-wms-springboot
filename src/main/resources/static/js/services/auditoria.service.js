/* ==========================================
   LogiTrack S.A. - Servicio de Auditoría
   ========================================== */

const AuditoriaService = {
    getAll() {
        return ApiService.get('/auditorias');
    },

    getById(id) {
        return ApiService.get(`/auditorias/${id}`);
    },

    filtrar(usuarioId, tipoOperacion) {
        let query = [];
        if (usuarioId) query.push(`usuarioId=${usuarioId}`);
        if (tipoOperacion) query.push(`tipoOperacion=${tipoOperacion}`);
        const queryString = query.length ? `?${query.join('&')}` : '';
        return ApiService.get(`/auditorias${queryString}`);
    }
};

window.AuditoriaService = AuditoriaService;
