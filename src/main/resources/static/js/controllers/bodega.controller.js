/* ==========================================
   LogiTrack S.A. - Bodega Controller Module
   ========================================== */

const BodegaModuleController = {
    async load() {
        try {
            const bodegas = await BodegaService.getAll();
            BodegaRenderer.renderTable(bodegas);
        } catch (err) {
            console.error('Error cargando bodegas:', err);
            Toast.error('Error al cargar la lista de bodegas');
        }
    },

    openCreateModal() {
        const form = document.getElementById('form-bodega');
        if (form) form.reset();
        document.getElementById('bodega-id').value = '';
        document.getElementById('modal-bodega-title').innerText = 'Nueva Bodega';
        App.openModal('modal-bodega');
    },

    async edit(id) {
        try {
            const bodega = await BodegaService.getById(id);
            document.getElementById('bodega-id').value = bodega.id;
            document.getElementById('bodega-nombre').value = bodega.nombre;
            document.getElementById('bodega-ubicacion').value = bodega.ubicacion;
            document.getElementById('bodega-capacidad').value = bodega.capacidad;
            document.getElementById('bodega-encargado').value = bodega.encargado;
            document.getElementById('bodega-activo').checked = bodega.activo !== false;
            document.getElementById('modal-bodega-title').innerText = 'Editar Bodega';
            App.openModal('modal-bodega');
        } catch (err) {
            Toast.error('No se pudo cargar la bodega');
        }
    },

    async save(formData) {
        const id = document.getElementById('bodega-id').value;
        try {
            if (id) {
                await BodegaService.update(id, formData);
                Toast.success('Bodega actualizada correctamente');
            } else {
                await BodegaService.create(formData);
                Toast.success('Bodega creada correctamente');
            }
            App.closeModal('modal-bodega');
            this.load();
        } catch (err) {
            Toast.error(err.message || 'Error al guardar la bodega');
        }
    },

    async delete(id) {
        if (!confirm('¿Estás seguro de desactivar/eliminar esta bodega?')) return;
        try {
            await BodegaService.delete(id);
            Toast.success('Bodega eliminada correctamente');
            this.load();
        } catch (err) {
            Toast.error(err.message || 'Error al eliminar la bodega');
        }
    }
};

window.BodegaModuleController = BodegaModuleController;
