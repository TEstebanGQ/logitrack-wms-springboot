/* ==========================================
   LogiTrack S.A. - Usuario Controller Module
   ========================================== */

const UsuarioModuleController = {
    usuariosCache: [],

    async load() {
        try {
            this.usuariosCache = await UsuarioService.getAll();
            UsuarioRenderer.renderTable(this.usuariosCache);
        } catch (err) {
            console.error('Error cargando usuarios:', err);
            Toast.error('Error al cargar la lista de usuarios');
        }
    },

    openCreateModal() {
        const form = document.getElementById('form-usuario');
        if (form) form.reset();
        document.getElementById('usuario-id').value = '';
        document.getElementById('usuario-pass-group').style.display = 'block';
        document.getElementById('usuario-password').required = true;
        document.getElementById('modal-usuario-title').innerText = 'Nuevo Usuario';
        App.openModal('modal-usuario');
    },

    edit(id) {
        const usuario = this.usuariosCache.find(u => u.id === id);
        if (!usuario) {
            Toast.error('Usuario no encontrado');
            return;
        }

        document.getElementById('usuario-id').value = usuario.id;
        document.getElementById('usuario-nombre').value = usuario.nombre;
        document.getElementById('usuario-apellido').value = usuario.apellido;
        document.getElementById('usuario-email').value = usuario.email;
        document.getElementById('usuario-rol').value = usuario.rol;
        document.getElementById('usuario-activo').checked = usuario.activo;
        
        // Contraseña opcional al editar
        document.getElementById('usuario-password').value = '';
        document.getElementById('usuario-password').required = false;
        document.getElementById('modal-usuario-title').innerText = 'Editar Usuario';
        App.openModal('modal-usuario');
    },

    async save(formData) {
        const id = document.getElementById('usuario-id').value;
        try {
            if (id) {
                await UsuarioService.update(id, formData);
                Toast.success('Usuario actualizado correctamente');
            } else {
                await UsuarioService.create(formData);
                Toast.success('Usuario creado correctamente');
            }
            App.closeModal('modal-usuario');
            this.load();
        } catch (err) {
            Toast.error(err.message || 'Error al guardar el usuario');
        }
    },

    async toggleStatus(id, newStatus) {
        const actionText = newStatus ? 'activar' : 'desactivar';
        const confirmed = await ConfirmDialog.show({
            title: `${newStatus ? 'Activar' : 'Desactivar'} Usuario`,
            message: `¿Estás seguro de ${actionText} este usuario?`,
            confirmText: `Sí, ${actionText}`,
            cancelText: 'Cancelar',
            type: newStatus ? 'primary' : 'danger'
        });
        if (!confirmed) return;

        try {
            await UsuarioService.update(id, { activo: newStatus });
            Toast.success(`Usuario ${newStatus ? 'activado' : 'desactivado'} correctamente`);
            this.load();
        } catch (err) {
            Toast.error(err.message || 'Error al cambiar estado del usuario');
        }
    },

    async delete(id) {
        const confirmed = await ConfirmDialog.show({
            title: 'Eliminar Usuario',
            message: '¿Estás seguro de eliminar este usuario? Si posee movimientos o auditorías asociadas, el sistema lo inhabilitará automáticamente para conservar el historial.',
            confirmText: 'Sí, Eliminar',
            cancelText: 'Cancelar',
            type: 'danger'
        });
        if (!confirmed) return;

        try {
            const res = await UsuarioService.delete(id);
            if (res && res.mensaje) {
                Toast.info(res.mensaje);
            } else {
                Toast.success('Usuario eliminado de la base de datos');
            }
            this.load();
        } catch (err) {
            Toast.error(err.message || 'Error al eliminar el usuario');
        }
    }
};

window.UsuarioModuleController = UsuarioModuleController;
