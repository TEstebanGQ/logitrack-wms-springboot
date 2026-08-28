/* ==========================================
   LogiTrack S.A. - Cliente Controller Module
   ========================================== */

const ClienteModuleController = {
    async load() {
        try {
            const clientes = await ClienteService.getAll(false);
            ClienteRenderer.renderTable(clientes);
        } catch (err) {
            console.error('Error cargando clientes:', err);
            Toast.error('Error al cargar la lista de clientes');
        }
    },

    openCreateModal() {
        const form = document.getElementById('form-cliente');
        if (form) form.reset();
        document.getElementById('cliente-id').value = '';
        document.getElementById('modal-cliente-title').innerText = 'Nuevo Cliente';
        App.openModal('modal-cliente');
    },

    async edit(id) {
        try {
            const cliente = await ClienteService.getById(id);
            document.getElementById('cliente-id').value = cliente.id;
            document.getElementById('cliente-nombre').value = cliente.nombre;
            document.getElementById('cliente-ruc').value = cliente.ruc || '';
            document.getElementById('cliente-telefono').value = cliente.telefono || '';
            document.getElementById('cliente-email').value = cliente.email || '';
            document.getElementById('cliente-direccion').value = cliente.direccion || '';
            document.getElementById('modal-cliente-title').innerText = 'Editar Cliente';
            App.openModal('modal-cliente');
        } catch (err) {
            Toast.error('No se pudo cargar el cliente');
        }
    },

    async save(formData) {
        const id = document.getElementById('cliente-id').value;
        try {
            if (id) {
                await ClienteService.update(id, formData);
                Toast.success('Cliente actualizado correctamente');
            } else {
                await ClienteService.create(formData);
                Toast.success('Cliente registrado correctamente');
            }
            App.closeModal('modal-cliente');
            this.load();
        } catch (err) {
            Toast.error(err.message || 'Error al guardar el cliente');
        }
    },

    async delete(id) {
        const confirmed = await ConfirmDialog.show({
            title: 'Desactivar Cliente',
            message: '¿Estás seguro de desactivar este cliente? No aparecerá en los nuevos despachos.',
            confirmText: 'Desactivar',
            cancelText: 'Cancelar',
            type: 'danger'
        });
        if (!confirmed) return;

        try {
            await ClienteService.delete(id);
            Toast.success('Cliente desactivado correctamente');
            this.load();
        } catch (err) {
            Toast.error(err.message || 'Error al desactivar el cliente');
        }
    },

    async verMovimientos(id, nombre) {
        try {
            const movimientos = await ClienteService.getMovimientos(id);
            const drawerTitle = document.getElementById('drawer-title');
            const drawerBadge = document.getElementById('drawer-badge');
            const drawerBody = document.getElementById('drawer-body');

            if (drawerTitle) drawerTitle.innerText = `Historial de Salidas — ${nombre}`;
            if (drawerBadge) drawerBadge.innerText = `CLIENTE #${id}`;

            if (!movimientos || movimientos.length === 0) {
                if (drawerBody) drawerBody.innerHTML = `<p style="padding:1.5rem; text-align:center; color:var(--text-muted);">No hay salidas ni despachos registrados a este cliente.</p>`;
            } else {
                const rows = movimientos.map(m => `
                    <div style="padding:12px; border-bottom:1px solid var(--border-color); background:rgba(255,255,255,0.02); border-radius:6px; margin-bottom:8px;">
                        <div style="display:flex; justify-content:space-between; align-items:center; margin-bottom:4px;">
                            <strong>Movimiento #${m.id}</strong>
                            <span class="badge badge-danger">SALIDA</span>
                        </div>
                        <div style="font-size:12px; color:var(--text-muted); margin-bottom:6px;">
                            Fecha: ${m.fecha ? new Date(m.fecha).toLocaleString() : 'N/A'} | Bodega: ${m.bodegaOrigen || 'N/A'}
                        </div>
                        <div style="font-size:12px;">
                            ${(m.detalles || []).map(d => `<div>• <strong>${d.productoNombre}</strong> (x${d.cantidad} u.) - $${(d.precioUnitario || 0).toLocaleString()}</div>`).join('')}
                        </div>
                    </div>
                `).join('');
                if (drawerBody) drawerBody.innerHTML = rows;
            }

            if (window.App && typeof window.App.openDrawer === 'function') {
                window.App.openDrawer();
            }
        } catch (err) {
            Toast.error('No se pudo cargar el historial de compras del cliente');
        }
    }
};

window.ClienteModuleController = ClienteModuleController;
