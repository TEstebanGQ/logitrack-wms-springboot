/* ==========================================
   LogiTrack S.A. - Proveedor Controller Module
   ========================================== */

const ProveedorModuleController = {
    async load() {
        try {
            const proveedores = await ProveedorService.getAll(false);
            ProveedorRenderer.renderTable(proveedores);
        } catch (err) {
            console.error('Error cargando proveedores:', err);
            Toast.error('Error al cargar la lista de proveedores');
        }
    },

    openCreateModal() {
        const form = document.getElementById('form-proveedor');
        if (form) form.reset();
        document.getElementById('proveedor-id').value = '';
        document.getElementById('modal-proveedor-title').innerText = 'Nuevo Proveedor';
        App.openModal('modal-proveedor');
    },

    async edit(id) {
        try {
            const proveedor = await ProveedorService.getById(id);
            document.getElementById('proveedor-id').value = proveedor.id;
            document.getElementById('proveedor-nombre').value = proveedor.nombre;
            document.getElementById('proveedor-ruc').value = proveedor.ruc || '';
            document.getElementById('proveedor-telefono').value = proveedor.telefono || '';
            document.getElementById('proveedor-email').value = proveedor.email || '';
            document.getElementById('proveedor-direccion').value = proveedor.direccion || '';
            document.getElementById('modal-proveedor-title').innerText = 'Editar Proveedor';
            App.openModal('modal-proveedor');
        } catch (err) {
            Toast.error('No se pudo cargar el proveedor');
        }
    },

    async save(formData) {
        const id = document.getElementById('proveedor-id').value;
        try {
            if (id) {
                await ProveedorService.update(id, formData);
                Toast.success('Proveedor actualizado correctamente');
            } else {
                await ProveedorService.create(formData);
                Toast.success('Proveedor registrado correctamente');
            }
            App.closeModal('modal-proveedor');
            this.load();
        } catch (err) {
            Toast.error(err.message || 'Error al guardar el proveedor');
        }
    },

    async delete(id) {
        const confirmed = await ConfirmDialog.show({
            title: 'Desactivar Proveedor',
            message: '¿Estás seguro de desactivar este proveedor? No aparecerá en las nuevas recepciones de mercancía.',
            confirmText: 'Desactivar',
            cancelText: 'Cancelar',
            type: 'danger'
        });
        if (!confirmed) return;

        try {
            await ProveedorService.delete(id);
            Toast.success('Proveedor desactivado correctamente');
            this.load();
        } catch (err) {
            Toast.error(err.message || 'Error al desactivar el proveedor');
        }
    },

    async verMovimientos(id, nombre) {
        try {
            const raw = await ProveedorService.getMovimientos(id);
            const movimientos = Array.isArray(raw) ? raw : (raw && Array.isArray(raw.content) ? raw.content : []);

            let htmlContent = '';
            if (movimientos.length === 0) {
                htmlContent = `<p style="padding:1.5rem; text-align:center; color:var(--text-muted);">No hay entradas ni suministros registrados de este proveedor.</p>`;
            } else {
                htmlContent = movimientos.map(m => `
                    <div style="padding:14px; border-bottom:1px solid var(--border); background:rgba(255,255,255,0.02); border-radius:8px; margin-bottom:10px;">
                        <div style="display:flex; justify-content:space-between; align-items:center; margin-bottom:6px;">
                            <strong style="color:var(--text-bright); font-size:14px;">Movimiento #${m.id}</strong>
                            <span class="badge badge-success">ENTRADA</span>
                        </div>
                        <div style="font-size:12px; color:var(--text-muted); margin-bottom:8px;">
                            📅 Fecha: ${m.fecha ? new Date(m.fecha).toLocaleString() : 'N/A'} | 🏬 Bodega: ${m.bodegaDestino || 'N/A'}
                        </div>
                        <div style="font-size:12.5px;">
                            ${(m.detalles || []).map(d => `<div>• <strong>${d.productoNombre}</strong> (x${d.cantidad} u.) - $${(d.precioUnitario || 0).toLocaleString()}</div>`).join('')}
                        </div>
                    </div>
                `).join('');
            }

            if (window.App && typeof window.App.openDrawer === 'function') {
                window.App.openDrawer(`Historial de Entradas — ${nombre}`, `PROVEEDOR #${id}`, 'badge-info', htmlContent);
            }
        } catch (err) {
            Toast.error('No se pudo cargar el historial de suministros del proveedor');
        }
    }
};

window.ProveedorModuleController = ProveedorModuleController;
