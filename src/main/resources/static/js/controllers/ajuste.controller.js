/* ==========================================
   LogiTrack S.A. - Controlador de Ajustes
   ========================================== */

const AjusteController = {
    async init() {
        try {
            const [ajustes, bodegas] = await Promise.all([
                AjusteService.getAll(),
                BodegaService.getAll(true)
            ]);

            AjusteRenderer.renderTabla(ajustes);

            const selectBodega = document.getElementById('filtro-ajuste-bodega');
            if (selectBodega && bodegas) {
                selectBodega.innerHTML = '<option value="">Todas las bodegas</option>' +
                    bodegas.map(b => `<option value="${b.id}">${b.nombre}</option>`).join('');
            }

            this.setupListeners();
        } catch (error) {
            console.error('Error al cargar la lista de ajustes:', error);
            if (typeof ToastService !== 'undefined') ToastService.error('Error al cargar ajustes de inventario');
        }
    },

    setupListeners() {
        const filtroBodega = document.getElementById('filtro-ajuste-bodega');
        const filtroTipo = document.getElementById('filtro-ajuste-tipo');

        const aplicarFiltros = async () => {
            try {
                const bodegaId = filtroBodega?.value;
                const tipo = filtroTipo?.value;

                if (bodegaId) {
                    const res = await AjusteService.getByBodega(bodegaId);
                    AjusteRenderer.renderTabla(tipo ? res.filter(a => a.tipoAjuste === tipo) : res);
                } else if (tipo) {
                    const res = await AjusteService.getByTipo(tipo);
                    AjusteRenderer.renderTabla(res);
                } else {
                    const res = await AjusteService.getAll();
                    AjusteRenderer.renderTabla(res);
                }
            } catch (error) {
                if (typeof ToastService !== 'undefined') ToastService.error('Error al filtrar ajustes');
            }
        };

        filtroBodega?.addEventListener('change', aplicarFiltros);
        filtroTipo?.addEventListener('change', aplicarFiltros);

        const txtBuscar = document.getElementById('buscador-ajuste-texto');
        txtBuscar?.addEventListener('input', async (e) => {
            const q = e.target.value.toLowerCase().trim();
            const todos = await AjusteService.getAll();
            const filtrados = todos.filter(a =>
                (a.productoNombre && a.productoNombre.toLowerCase().includes(q)) ||
                (a.bodegaNombre && a.bodegaNombre.toLowerCase().includes(q)) ||
                (a.justificacion && a.justificacion.toLowerCase().includes(q)) ||
                (a.usuarioNombre && a.usuarioNombre.toLowerCase().includes(q))
            );
            AjusteRenderer.renderTabla(filtrados);
        });

        const btnExport = document.getElementById('btn-exportar-ajustes-csv');
        btnExport?.addEventListener('click', async () => {
            try {
                const ajustes = await AjusteService.getAll();
                const headers = [
                    { label: 'ID', key: 'id' },
                    { label: 'Fecha', key: (a) => a.fecha ? new Date(a.fecha).toLocaleString() : '' },
                    { label: 'Bodega', key: 'bodegaNombre' },
                    { label: 'Producto', key: 'productoNombre' },
                    { label: 'Tipo', key: 'tipoAjuste' },
                    { label: 'Stock Anterior', key: 'cantidadAnterior' },
                    { label: 'Stock Nuevo', key: 'cantidadNueva' },
                    { label: 'Diferencia', key: 'diferencia' },
                    { label: 'Justificación', key: 'justificacion' },
                    { label: 'Usuario', key: 'usuarioNombre' }
                ];
                if (typeof ExportService !== 'undefined') {
                    ExportService.exportarCSV(ajustes, headers, `logitrack-ajustes-${new Date().toISOString().split('T')[0]}.csv`);
                }
            } catch (err) {
                if (typeof ToastService !== 'undefined') ToastService.error('Error al exportar ajustes');
            }
        });
    }
};

window.AjusteController = AjusteController;
