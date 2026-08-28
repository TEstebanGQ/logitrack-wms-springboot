/* ==========================================
   LogiTrack S.A. - Conteos Cíclicos Service
   ========================================== */

const ConteoService = {
    getAll() {
        return ApiService.get('/conteos-ciclicos');
    },

    getById(id) {
        return ApiService.get(`/conteos-ciclicos/${id}`);
    },

    crear(data) {
        return ApiService.post('/conteos-ciclicos', data);
    },

    registrarConteoFisico(conteoId, detalleId, stockFisico, notas) {
        let url = `/conteos-ciclicos/${conteoId}/detalles/${detalleId}/contar?stockFisico=${stockFisico}`;
        if (notas) url += `&notas=${encodeURIComponent(notas)}`;
        return ApiService.patch(url, {});
    },

    conciliar(conteoId) {
        return ApiService.post(`/conteos-ciclicos/${conteoId}/conciliar`, {});
    }
};

window.ConteoService = ConteoService;
