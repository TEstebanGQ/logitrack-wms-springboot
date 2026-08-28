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

    async verInventario(id) {
        try {
            const bodega = await BodegaService.getById(id);
            const inventario = await BodegaService.getInventario(id);

            const titleEl = document.getElementById('inv-bodega-title');
            const subEl = document.getElementById('inv-bodega-subtitle');
            const bodyEl = document.getElementById('inv-bodega-body');

            if (titleEl) titleEl.innerText = `📦 Inventario Almacenado — ${bodega.nombre}`;
            if (subEl) subEl.innerText = `Ubicación: ${bodega.ubicacion} | Encargado: ${bodega.encargado} | Capacidad Max: ${bodega.capacidad ? bodega.capacidad.toLocaleString() : 0} u.`;

            if (!inventario || inventario.length === 0) {
                if (bodyEl) bodyEl.innerHTML = `<p style="padding:1.5rem; text-align:center; color:var(--text-muted);">Esta bodega no cuenta con productos almacenados actualmente.</p>`;
            } else {
                const rows = inventario.map(item => {
                    let badgeClass = 'badge-success';
                    let statusText = 'Stock Disponible';
                    if (item.stockActual === 0) {
                        badgeClass = 'badge-danger';
                        statusText = 'Agotado en Bodega';
                    } else if (item.stockActual < 10) {
                        badgeClass = 'badge-warning';
                        statusText = 'Stock Bajo en Bodega';
                    }

                    return `
                        <tr>
                            <td>#${item.productoId}</td>
                            <td><strong style="color:var(--text-bright);">${item.productoNombre}</strong></td>
                            <td><strong style="font-family:var(--font-mono); font-size:14px; color:var(--accent-color);">${item.stockActual} u.</strong></td>
                            <td><span class="badge ${badgeClass}">${statusText}</span></td>
                        </tr>
                    `;
                }).join('');

                if (bodyEl) {
                    bodyEl.innerHTML = `
                        <div class="table-container">
                            <table class="table">
                                <thead>
                                    <tr>
                                        <th>ID Producto</th>
                                        <th>Nombre del Producto</th>
                                        <th>Stock Físico en Bodega</th>
                                        <th>Estado de Disponibilidad</th>
                                    </tr>
                                </thead>
                                <tbody>${rows}</tbody>
                            </table>
                        </div>
                    `;
                }
            }

            App.openModal('modal-inventario-bodega');
        } catch (err) {
            console.error('Error cargando inventario de la bodega:', err);
            Toast.error('No se pudo cargar el inventario de la bodega');
        }
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
            if (window.App && typeof window.App.loadDashboardData === 'function') {
                window.App.loadDashboardData();
            }
        } catch (err) {
            Toast.error(err.message || 'Error al guardar la bodega');
        }
    },

    async delete(id) {
        const confirmed = await ConfirmDialog.show({
            title: 'Desactivar Bodega',
            message: '¿Estás seguro de desactivar/eliminar esta bodega? Quedará inactiva en el sistema.',
            confirmText: 'Desactivar Bodega',
            cancelText: 'Cancelar',
            type: 'danger'
        });
        if (!confirmed) return;

        try {
            await BodegaService.delete(id);
            Toast.success('Bodega eliminada correctamente');
            this.load();
            if (window.App && typeof window.App.loadDashboardData === 'function') {
                window.App.loadDashboardData();
            }
        } catch (err) {
            Toast.error(err.message || 'Error al eliminar la bodega');
        }
    }
};

window.BodegaModuleController = BodegaModuleController;
