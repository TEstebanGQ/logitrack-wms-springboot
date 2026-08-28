/* ==========================================
   LogiTrack S.A. - WMS Auxiliares Service
   ========================================== */

const WmsAuxService = {
    // Unidades de Medida
    getUnidades() {
        return ApiService.get('/unidades-medida');
    },
    crearUnidad(data) {
        return ApiService.post('/unidades-medida', data);
    },

    // Series
    getSeries() {
        return ApiService.get('/series');
    },
    crearSerie(data) {
        return ApiService.post('/series', data);
    },
    actualizarEstadoSerie(id, nuevoEstado, observaciones) {
        let url = `/series/${id}/estado?nuevoEstado=${encodeURIComponent(nuevoEstado)}`;
        if (observaciones) url += `&observaciones=${encodeURIComponent(observaciones)}`;
        return ApiService.patch(url, {});
    },

    // Zonas
    getZonas() {
        return ApiService.get('/zonas');
    },
    crearZona(data) {
        return ApiService.post('/zonas', data);
    },

    // Tipos Ubicación
    getTiposUbicacion() {
        return ApiService.get('/tipos-ubicacion');
    },
    crearTipoUbicacion(data) {
        return ApiService.post('/tipos-ubicacion', data);
    }
};

window.WmsAuxService = WmsAuxService;
