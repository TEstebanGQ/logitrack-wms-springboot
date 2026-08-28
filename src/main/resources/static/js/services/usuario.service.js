/* ==========================================
   LogiTrack S.A. - Servicio de Usuarios
   ========================================== */

const UsuarioService = {
    getAll() {
        return ApiService.get('/usuarios');
    },

    create(usuarioData) {
        return ApiService.post('/usuarios', usuarioData);
    },

    update(id, usuarioData) {
        return ApiService.put(`/usuarios/${id}`, usuarioData);
    },

    delete(id) {
        return ApiService.delete(`/usuarios/${id}`);
    }
};

window.UsuarioService = UsuarioService;
