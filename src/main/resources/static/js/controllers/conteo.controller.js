/* ==========================================
   LogiTrack S.A. - Conteo Cíclico Module Controller
   ========================================== */

const ConteoController = {
    conteosList: [],
    conteoActualId: null,

    async load() {
        try {
            this.conteosList = await ConteoService.getAll().catch(() => []);
            ConteoRenderer.renderTable(this.conteosList);
        } catch (err) {
            console.error('Error al cargar conteos:', err);
            Toast.error('Error al cargar auditorías');
        }
    },

    async abrirModalProgramar() {
        const [bodegas, productos, zonas] = await Promise.all([
            typeof BodegaService !== 'undefined' ? BodegaService.getAll().catch(() => []) : [],
            typeof ProductoService !== 'undefined' ? ProductoService.getAll().catch(() => []) : [],
            typeof WmsAuxService !== 'undefined' ? WmsAuxService.getZonas().catch(() => []) : []
        ]);

        const bodegasList = Array.isArray(bodegas) ? bodegas : (bodegas && Array.isArray(bodegas.content) ? bodegas.content : []);
        const productosList = Array.isArray(productos) ? productos : (productos && Array.isArray(productos.content) ? productos.content : []);
        const zonasList = Array.isArray(zonas) ? zonas : (zonas && Array.isArray(zonas.content) ? zonas.content : []);

        const selBodega = document.getElementById('conteo-bodega');
        const selZona = document.getElementById('conteo-zona');
        const selProd = document.getElementById('conteo-producto');

        if (selBodega) selBodega.innerHTML = bodegasList.map(b => `<option value="${b.id}">${b.nombre}</option>`).join('');
        if (selZona) {
            selZona.innerHTML = '<option value="">-- Toda la bodega (General) --</option>' +
                zonasList.map(z => `<option value="${z.id}">${z.nombre} (${z.tipoZona || z.codigoZona})</option>`).join('');
        }
        if (selProd) selProd.innerHTML = productosList.map(p => `<option value="${p.id}">${p.nombre} (Stock: ${p.stock})</option>`).join('');

        const modal = document.getElementById('modal-conteo');
        if (modal) modal.style.display = 'flex';
    },

    cerrarModalProgramar() {
        const modal = document.getElementById('modal-conteo');
        if (modal) modal.style.display = 'none';
        document.getElementById('form-conteo')?.reset();
    },

    async guardarProgramacion(e) {
        e.preventDefault();
        try {
            const zonaVal = document.getElementById('conteo-zona')?.value;
            const data = {
                bodegaId: parseInt(document.getElementById('conteo-bodega').value),
                zonaId: zonaVal ? parseInt(zonaVal) : null,
                fechaProgramada: document.getElementById('conteo-fecha').value,
                observaciones: document.getElementById('conteo-obs').value,
                detalles: [
                    {
                        productoId: parseInt(document.getElementById('conteo-producto').value),
                        stockFisico: null
                    }
                ]
            };


            await ConteoService.crear(data);
            Toast.success('Auditoría cíclica programada exitosamente.');
            this.cerrarModalProgramar();
            this.load();
        } catch (err) {
            Toast.error(err.response?.data?.message || 'Error al programar auditoría');
        }
    },

    async verDetalles(id) {
        this.conteoActualId = id;
        try {
            const conteo = await ConteoService.getById(id);
            ConteoRenderer.renderDetallesModal(conteo);
            const modal = document.getElementById('modal-conteo-detalles');
            if (modal) modal.style.display = 'flex';
        } catch (err) {
            Toast.error('Error al cargar detalles de la auditoría');
        }
    },

    cerrarModalDetalles() {
        const modal = document.getElementById('modal-conteo-detalles');
        if (modal) modal.style.display = 'none';
        this.conteoActualId = null;
    },

    async actualizarConteoFisico(conteoId, detalleId, stockFisico) {
        try {
            await ConteoService.registrarConteoFisico(conteoId, detalleId, parseInt(stockFisico));
            Toast.info('Línea de conteo actualizada.');
            const conteo = await ConteoService.getById(conteoId);
            ConteoRenderer.renderDetallesModal(conteo);
            this.load();
        } catch (err) {
            Toast.error('Error al actualizar conteo físico');
        }
    },

    async conciliarConteo() {
        const ok = await ConfirmDialog.show({ title: 'Conciliar Auditoría Cíclica', message: '¿Deseas conciliar las diferencias y generar automáticamente los ajustes de inventario pertinentes?', type: 'info', confirmText: 'Sí, Conciliar' });
        if (!ok) return;
        try {
            await ConteoService.conciliar(this.conteoActualId);
            Toast.success('Auditoría conciliada y stock ajustado exitosamente.');
            this.cerrarModalDetalles();
            this.load();
        } catch (err) {
            Toast.error('Error al conciliar auditoría');
        }
    }
};

window.ConteoController = ConteoController;
