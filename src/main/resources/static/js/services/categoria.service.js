/* ==========================================
   LogiTrack S.A. - Servicio de Categorías
   ========================================== */

const CategoriaService = {
    getAll() {
        return ApiService.get('/categorias');
    },

    create(categoriaData) {
        return ApiService.post('/categorias', categoriaData);
    }
};

window.CategoriaService = CategoriaService;
