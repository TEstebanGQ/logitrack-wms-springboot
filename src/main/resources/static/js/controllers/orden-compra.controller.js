/* ==========================================
   LogiTrack S.A. - Controlador de Órdenes de Compra
   ========================================== */

const OrdenCompraController = {
    async init() {
        try {
            const raw = await OrdenCompraService.getAll();
            const ordenes = Array.isArray(raw) ? raw : (raw && Array.isArray(raw.content) ? raw.content : []);
            OrdenCompraRenderer.renderTabla(ordenes);
            this.setupListeners();
        } catch (error) {
            console.error('Error al cargar órdenes de compra:', error);
            if (typeof ToastService !== 'undefined') ToastService.error('Error al cargar las órdenes de compra');
        }
    },

    async aprobar(id) {
        const ok = await ConfirmDialog.show({ title: 'Aprobar Orden', message: '¿Confirmas la aprobación de esta orden de compra?', type: 'info', confirmText: 'Sí, Aprobar' });
        if (ok) {
            try {
                await OrdenCompraService.aprobar(id);
                if (typeof ToastService !== 'undefined') ToastService.success('Orden de compra aprobada exitosamente');
                this.init();
            } catch (err) {
                if (typeof ToastService !== 'undefined') ToastService.error(err.message || 'Error al aprobar orden');
            }
        }
    },

    async cancelar(id) {
        const motivo = await ConfirmDialog.prompt({ title: 'Cancelar Orden de Compra', message: 'Ingresa el motivo de cancelación de la orden:', placeholder: 'Ej. Cambio de especificación...' });
        if (motivo !== null) {
            try {
                await OrdenCompraService.cancelar(id, motivo);
                if (typeof ToastService !== 'undefined') ToastService.success('Orden de compra cancelada');
                this.init();
            } catch (err) {
                if (typeof ToastService !== 'undefined') ToastService.error(err.message || 'Error al cancelar orden');
            }
        }
    },

    async recibir(id) {
        const ok = await ConfirmDialog.show({ title: 'Recibir Mercancía', message: '¿Deseas recibir la mercancía e ingresar automáticamente el inventario a bodega?', type: 'info', confirmText: 'Sí, Recibir' });
        if (ok) {
            try {
                await OrdenCompraService.recibir(id);
                if (typeof ToastService !== 'undefined') ToastService.success('Mercancía recibida e inventario actualizado');
                this.init();
            } catch (err) {
                if (typeof ToastService !== 'undefined') ToastService.error(err.message || 'Error al recibir orden');
            }
        }
    },

    setupListeners() {
        const filtroEstado = document.getElementById('filtro-orden-estado');
        filtroEstado?.addEventListener('change', async (e) => {
            try {
                const estado = e.target.value;
                const res = estado ? await OrdenCompraService.getByEstado(estado) : await OrdenCompraService.getAll();
                OrdenCompraRenderer.renderTabla(res);
            } catch (error) {
                if (typeof ToastService !== 'undefined') ToastService.error('Error al filtrar órdenes');
            }
        });

        const txtBuscar = document.getElementById('buscador-orden-texto');
        txtBuscar?.addEventListener('input', async (e) => {
            const q = e.target.value.toLowerCase().trim();
            const todas = await OrdenCompraService.getAll();
            const filtradas = todas.filter(o =>
                (o.codigoOrden && o.codigoOrden.toLowerCase().includes(q)) ||
                (o.proveedorNombre && o.proveedorNombre.toLowerCase().includes(q)) ||
                (o.bodegaDestinoNombre && o.bodegaDestinoNombre.toLowerCase().includes(q)) ||
                (o.usuarioSolicitante && o.usuarioSolicitante.toLowerCase().includes(q))
            );
            OrdenCompraRenderer.renderTabla(filtradas);
        });

        const btnExport = document.getElementById('btn-exportar-ordenes-csv');
        btnExport?.addEventListener('click', async () => {
            try {
                const ordenes = await OrdenCompraService.getAll();
                const headers = [
                    { label: 'Código', key: 'codigoOrden' },
                    { label: 'Fecha Solicitud', key: (o) => o.fechaSolicitud ? new Date(o.fechaSolicitud).toLocaleDateString() : '' },
                    { label: 'Proveedor', key: 'proveedorNombre' },
                    { label: 'Bodega Destino', key: 'bodegaDestinoNombre' },
                    { label: 'Total Estimado', key: 'totalEstimado' },
                    { label: 'Estado', key: 'estado' },
                    { label: 'Fecha Entrega', key: 'fechaEntregaEsperada' },
                    { label: 'Solicitante', key: 'usuarioSolicitante' }
                ];
                if (typeof ExportService !== 'undefined') {
                    ExportService.exportarCSV(ordenes, headers, `logitrack-ordenes-compra-${new Date().toISOString().split('T')[0]}.csv`);
                }
            } catch (err) {
                if (typeof ToastService !== 'undefined') ToastService.error('Error al exportar órdenes de compra');
            }
        });
    }
};

window.OrdenCompraController = OrdenCompraController;
window.ordenCompraController = OrdenCompraController;
