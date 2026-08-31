/* ==========================================
   LogiTrack S.A. - Zonas & Series Module Controller
   ========================================== */

const ZonasSeriesController = {
    seriesList: [],
    zonasList: [],
    unidadesList: [],

    async load() {
        try {
            const [series, zonas, unidades] = await Promise.all([
                WmsAuxService.getSeries().catch(() => []),
                WmsAuxService.getZonas().catch(() => []),
                WmsAuxService.getUnidades().catch(() => [])
            ]);

            this.seriesList = series;
            this.zonasList = zonas;
            this.unidadesList = unidades;

            ZonasSeriesRenderer.renderSeries(series);
            ZonasSeriesRenderer.renderZonas(zonas);
            ZonasSeriesRenderer.renderUnidades(unidades);
        } catch (err) {
            console.error('Error al cargar zonas y series:', err);
            Toast.error('Error al cargar datos auxiliares WMS');
        }
    },

    // Series
    async abrirModalSerie() {
        const [productos, bodegas] = await Promise.all([
            typeof ProductoService !== 'undefined' ? ProductoService.getAll().catch(() => []) : [],
            typeof BodegaService !== 'undefined' ? BodegaService.getAll().catch(() => []) : []
        ]);

        const productosList = Array.isArray(productos) ? productos : (productos && Array.isArray(productos.content) ? productos.content : []);
        const bodegasList = Array.isArray(bodegas) ? bodegas : (bodegas && Array.isArray(bodegas.content) ? bodegas.content : []);

        const selProd = document.getElementById('serie-producto');
        const selBodega = document.getElementById('serie-bodega');

        if (selProd) selProd.innerHTML = productosList.map(p => `<option value="${p.id}">${p.nombre}</option>`).join('');
        if (selBodega) selBodega.innerHTML = bodegasList.map(b => `<option value="${b.id}">${b.nombre}</option>`).join('');

        const modal = document.getElementById('modal-serie');
        if (modal) modal.style.display = 'flex';
    },

    cerrarModalSerie() {
        const modal = document.getElementById('modal-serie');
        if (modal) modal.style.display = 'none';
        document.getElementById('form-serie')?.reset();
    },

    async guardarSerie(e) {
        e.preventDefault();
        try {
            const data = {
                numeroSerie: document.getElementById('serie-numero').value,
                productoId: parseInt(document.getElementById('serie-producto').value),
                bodegaId: parseInt(document.getElementById('serie-bodega').value),
                observaciones: document.getElementById('serie-obs').value
            };

            await WmsAuxService.crearSerie(data);
            Toast.success('Número de serie registrado.');
            this.cerrarModalSerie();
            this.load();
        } catch (err) {
            Toast.error(err.response?.data?.message || 'Error al guardar serie');
        }
    },

    // Zonas
    async abrirModalZona() {
        const bodegas = typeof BodegaService !== 'undefined' ? await BodegaService.getAll().catch(() => []) : [];
        const selBodega = document.getElementById('zona-bodega');
        if (selBodega) selBodega.innerHTML = bodegas.map(b => `<option value="${b.id}">${b.nombre}</option>`).join('');

        const modal = document.getElementById('modal-zona');
        if (modal) modal.style.display = 'flex';
    },

    cerrarModalZona() {
        const modal = document.getElementById('modal-zona');
        if (modal) modal.style.display = 'none';
        document.getElementById('form-zona')?.reset();
    },

    async guardarZona(e) {
        e.preventDefault();
        try {
            const data = {
                codigo: document.getElementById('zona-codigo').value,
                nombre: document.getElementById('zona-nombre').value,
                tipoZona: document.getElementById('zona-tipo').value,
                bodegaId: parseInt(document.getElementById('zona-bodega').value)
            };

            await WmsAuxService.crearZona(data);
            Toast.success('Zona creada.');
            this.cerrarModalZona();
            this.load();
        } catch (err) {
            Toast.error(err.response?.data?.message || 'Error al crear zona');
        }
    },

    // Unidades
    abrirModalUnidad() {
        const modal = document.getElementById('modal-unidad');
        if (modal) modal.style.display = 'flex';
    },

    cerrarModalUnidad() {
        const modal = document.getElementById('modal-unidad');
        if (modal) modal.style.display = 'none';
        document.getElementById('form-unidad')?.reset();
    },

    async guardarUnidad(e) {
        e.preventDefault();
        try {
            const data = {
                codigo: document.getElementById('unidad-codigo').value,
                nombre: document.getElementById('unidad-nombre').value,
                factorConversion: parseFloat(document.getElementById('unidad-factor').value || 1)
            };

            await WmsAuxService.crearUnidad(data);
            Toast.success('Unidad de medida guardada.');
            this.cerrarModalUnidad();
            this.load();
        } catch (err) {
            Toast.error(err.response?.data?.message || 'Error al guardar unidad');
        }
    }
};

window.ZonasSeriesController = ZonasSeriesController;
