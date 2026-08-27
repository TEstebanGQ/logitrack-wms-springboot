/* ==========================================
   LogiTrack S.A. - Producto Controller Module
   ========================================== */

const ProductoModuleController = {
    async load(filtroStockBajo = false) {
        try {
            let productos;
            if (filtroStockBajo) {
                productos = await ProductoService.getStockBajo(10);
            } else {
                productos = await ProductoService.getAll();
            }
            ProductoRenderer.renderTable(productos);
        } catch (err) {
            console.error('Error cargando productos:', err);
            Toast.error('Error al cargar productos');
        }
    },

    async openCreateModal() {
        const form = document.getElementById('form-producto');
        if (form) form.reset();
        document.getElementById('producto-id').value = '';
        document.getElementById('modal-producto-title').innerText = 'Nuevo Producto';
        await this.populateCategoriaSelect();
        App.openModal('modal-producto');
    },

    async edit(id) {
        try {
            const producto = await ProductoService.getById(id);
            await this.populateCategoriaSelect(producto.categoriaId);
            document.getElementById('producto-id').value = producto.id;
            document.getElementById('producto-nombre').value = producto.nombre;
            if (document.getElementById('producto-categoria-id')) {
                document.getElementById('producto-categoria-id').value = producto.categoriaId || '';
            }
            document.getElementById('producto-stock').value = producto.stock;
            document.getElementById('producto-precio').value = producto.precio;
            document.getElementById('producto-descripcion').value = producto.descripcion || '';
            document.getElementById('modal-producto-title').innerText = 'Editar Producto';
            App.openModal('modal-producto');
        } catch (err) {
            Toast.error('No se pudo cargar el producto');
        }
    },

    async save(formData) {
        const id = document.getElementById('producto-id').value;
        try {
            if (id) {
                await ProductoService.update(id, formData);
                Toast.success('Producto actualizado correctamente');
            } else {
                await ProductoService.create(formData);
                Toast.success('Producto creado correctamente');
            }
            App.closeModal('modal-producto');
            this.load();
        } catch (err) {
            Toast.error(err.message || 'Error al guardar el producto');
        }
    },

    async delete(id) {
        const confirmed = await ConfirmDialog.show({
            title: 'Desactivar Producto',
            message: '¿Estás seguro de desactivar/eliminar este producto del catálogo?',
            confirmText: 'Eliminar Producto',
            cancelText: 'Cancelar',
            type: 'danger'
        });
        if (!confirmed) return;

        try {
            await ProductoService.delete(id);
            Toast.success('Producto eliminado correctamente');
            this.load();
        } catch (err) {
            Toast.error(err.message || 'Error al eliminar el producto');
        }
    },

    async populateCategoriaSelect(selectedId = null) {
        try {
            const categorias = await CategoriaService.getAll().catch(() => []);
            const catSelect = document.getElementById('producto-categoria-id');
            if (catSelect) {
                catSelect.innerHTML = categorias.map(c =>
                    `<option value="${c.id}" ${c.id === selectedId ? 'selected' : ''}>${c.nombre}</option>`
                ).join('');
            }
        } catch (err) {
            console.error('Error cargando categorías:', err);
        }
    }
};

window.ProductoModuleController = ProductoModuleController;
