/* ==========================================
   LogiTrack S.A. - Servicio de Categorías
   ========================================== */

const CategoriaService = {
    getAll() {
        return ApiService.get('/categorias');
    }
};

window.CategoriaService = CategoriaService;
