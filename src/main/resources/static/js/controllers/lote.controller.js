/* ==========================================
   LogiTrack S.A. - Controlador de Lotes y Vencimientos
   ========================================== */

const LoteController = {
    async init() {
        try {
            const [lotesRaw, productosRaw, bodegasRaw] = await Promise.all([
                LoteService.getAll(),
                ProductoService.getAll(),
                BodegaService.getAll(true)
            ]);

            const lotes = Array.isArray(lotesRaw) ? lotesRaw : (lotesRaw && Array.isArray(lotesRaw.content) ? lotesRaw.content : []);
            const productos = Array.isArray(productosRaw) ? productosRaw : (productosRaw && Array.isArray(productosRaw.content) ? productosRaw.content : []);
            const bodegas = Array.isArray(bodegasRaw) ? bodegasRaw : (bodegasRaw && Array.isArray(bodegasRaw.content) ? bodegasRaw.content : []);

            LoteRenderer.renderTabla(lotes);

            const selectProd = document.getElementById('filtro-lote-producto');
            if (selectProd && productos) {
                selectProd.innerHTML = '<option value="">Todos los productos</option>' +
                    productos.map(p => `<option value="${p.id}">${p.nombre}</option>`).join('');
            }

            const selectBodega = document.getElementById('filtro-lote-bodega');
            if (selectBodega && bodegas) {
                selectBodega.innerHTML = '<option value="">Todas las bodegas</option>' +
                    bodegas.map(b => `<option value="${b.id}">${b.nombre}</option>`).join('');
            }

            this.setupListeners();
        } catch (error) {
            console.error('Error al cargar lotes:', error);
            if (typeof ToastService !== 'undefined') ToastService.error('Error al cargar los lotes');
        }
    },

    async cambiarEstado(id) {
        const nuevoEstado = await ConfirmDialog.select({
            title: 'Cambiar Estado de Lote',
            message: 'Selecciona el nuevo estado operacional del lote:',
            options: [
                { value: 'DISPONIBLE', label: 'DISPONIBLE — Apto para rotación y despacho' },
                { value: 'CUARENTENA', label: 'CUARENTENA — Bloqueado por calidad / revisión' },
                { value: 'VENCIDO', label: 'VENCIDO — Caducado / Dado de baja' },
                { value: 'AGOTADO', label: 'AGOTADO — Sin existencias físicas' }
            ],
            defaultValue: 'DISPONIBLE',
            confirmText: 'Actualizar Estado'
        });

        if (nuevoEstado) {
            try {
                await LoteService.updateEstado(id, nuevoEstado);
                if (typeof ToastService !== 'undefined') ToastService.success('Estado del lote actualizado exitosamente');
                this.init();
            } catch (err) {
                if (typeof ToastService !== 'undefined') ToastService.error(err.message || 'Error al actualizar estado del lote');
            }
        }
    },


    setupListeners() {
        const filtroBodega = document.getElementById('filtro-lote-bodega');
        filtroBodega?.addEventListener('change', async (e) => {
            try {
                const bodegaId = e.target.value;
                const res = bodegaId ? await LoteService.getByBodega(bodegaId) : await LoteService.getAll();
                LoteRenderer.renderTabla(res);
            } catch (error) {
                if (typeof ToastService !== 'undefined') ToastService.error('Error al filtrar lotes');
            }
        });

        const txtBuscar = document.getElementById('buscador-lote-texto');
        txtBuscar?.addEventListener('input', async (e) => {
            const q = e.target.value.toLowerCase().trim();
            const todos = await LoteService.getAll();
            const filtrados = todos.filter(l =>
                (l.codigoLote && l.codigoLote.toLowerCase().includes(q)) ||
                (l.productoNombre && l.productoNombre.toLowerCase().includes(q)) ||
                (l.bodegaNombre && l.bodegaNombre.toLowerCase().includes(q))
            );
            LoteRenderer.renderTabla(filtrados);
        });

        const btnProximos = document.getElementById('btn-ver-proximos-vencer');
        btnProximos?.addEventListener('click', async () => {
            try {
                const res = await LoteService.getProximosVencer(30);
                LoteRenderer.renderTabla(res);
                if (typeof ToastService !== 'undefined') ToastService.info(`Se encontraron ${res.length} lotes próximos a vencer`);
            } catch (error) {
                if (typeof ToastService !== 'undefined') ToastService.error('Error al consultar lotes próximos a vencer');
            }
        });

        const btnExport = document.getElementById('btn-exportar-lotes-csv');
        btnExport?.addEventListener('click', async () => {
            try {
                const lotes = await LoteService.getAll();
                const headers = [
                    { label: 'Código Lote', key: 'codigoLote' },
                    { label: 'Producto', key: 'productoNombre' },
                    { label: 'Bodega', key: 'bodegaNombre' },
                    { label: 'Stock Actual', key: 'stockActual' },
                    { label: 'Stock Inicial', key: 'stockInicial' },
                    { label: 'Fecha Fabricación', key: 'fechaFabricacion' },
                    { label: 'Fecha Vencimiento', key: 'fechaVencimiento' },
                    { label: 'Estado', key: 'estado' }
                ];
                if (typeof ExportService !== 'undefined') {
                    ExportService.exportarCSV(lotes, headers, `logitrack-lotes-${new Date().toISOString().split('T')[0]}.csv`);
                }
            } catch (err) {
                if (typeof ToastService !== 'undefined') ToastService.error('Error al exportar lotes');
            }
        });
    }
};

window.LoteController = LoteController;
window.loteController = LoteController;
