/* ==========================================
   LogiTrack S.A. - Reporte Controller Module
   ========================================== */

const ReporteModuleController = {
    currentTab: 'resumen',

    async load() {
        try {
            await this.loadResumen();
            await this.populateSelects();
            this.initEventListeners();
            this.initExportButtons();
        } catch (err) {
            console.error('Error cargando reportes:', err);
            Toast.error('Error al cargar datos del módulo de reportes');
        }
    },

    async loadResumen() {
        const reporte = await ReporteService.getResumenGeneral().catch(() => null);
        ReporteRenderer.renderSection(reporte);
    },

    async populateSelects() {
        const [bodegas, productos] = await Promise.all([
            typeof BodegaService !== 'undefined' ? BodegaService.getAll().catch(() => []) : [],
            typeof ProductoService !== 'undefined' ? ProductoService.getAll().catch(() => []) : []
        ]);

        const selBodegaMov = document.getElementById('filtro-rep-mov-bodega');
        const selProdMov = document.getElementById('filtro-rep-mov-producto');
        const selProdAud = document.getElementById('filtro-rep-aud-producto');

        if (selBodegaMov) {
            selBodegaMov.innerHTML = '<option value="">Todas las bodegas</option>' +
                bodegas.map(b => `<option value="${b.id}">${b.nombre}</option>`).join('');
        }
        if (selProdMov) {
            selProdMov.innerHTML = '<option value="">Todos los productos</option>' +
                productos.map(p => `<option value="${p.id}">${p.nombre}</option>`).join('');
        }
        if (selProdAud) {
            selProdAud.innerHTML = '<option value="">Cualquier recurso / producto</option>' +
                productos.map(p => `<option value="${p.id}">${p.nombre}</option>`).join('');
        }
    },

    switchTab(tabName) {
        this.currentTab = tabName;

        document.querySelectorAll('.btn-reporte-tab').forEach(b => b.classList.remove('btn-primary', 'active'));
        document.querySelectorAll('.btn-reporte-tab').forEach(b => b.classList.add('btn-secondary'));

        const activeBtn = document.getElementById(`btn-tab-${tabName}`);
        if (activeBtn) {
            activeBtn.classList.remove('btn-secondary');
            activeBtn.classList.add('btn-primary', 'active');
        }

        document.querySelectorAll('.reporte-tab-content').forEach(c => c.style.display = 'none');
        const activeContent = document.getElementById(`tab-content-${tabName}`);
        if (activeContent) activeContent.style.display = 'block';

        if (tabName === 'movimientos') {
            this.filtrarMovimientos();
        } else if (tabName === 'auditoria') {
            this.filtrarAuditoria();
        }
    },

    limpiarFiltrosMovimientos() {
        if (document.getElementById('filtro-rep-mov-bodega')) document.getElementById('filtro-rep-mov-bodega').value = '';
        if (document.getElementById('filtro-rep-mov-producto')) document.getElementById('filtro-rep-mov-producto').value = '';
        if (document.getElementById('filtro-rep-mov-tipo')) document.getElementById('filtro-rep-mov-tipo').value = '';
        if (document.getElementById('filtro-rep-mov-desde')) document.getElementById('filtro-rep-mov-desde').value = '';
        if (document.getElementById('filtro-rep-mov-hasta')) document.getElementById('filtro-rep-mov-hasta').value = '';
        this.filtrarMovimientos();
    },

    limpiarFiltrosAuditoria() {
        if (document.getElementById('filtro-rep-aud-producto')) document.getElementById('filtro-rep-aud-producto').value = '';
        if (document.getElementById('filtro-rep-aud-campo')) document.getElementById('filtro-rep-aud-campo').value = '';
        if (document.getElementById('filtro-rep-aud-desde')) document.getElementById('filtro-rep-aud-desde').value = '';
        if (document.getElementById('filtro-rep-aud-hasta')) document.getElementById('filtro-rep-aud-hasta').value = '';
        this.filtrarAuditoria();
    },

    async filtrarMovimientos() {
        try {
            const params = {
                bodega: document.getElementById('filtro-rep-mov-bodega')?.value || '',
                producto: document.getElementById('filtro-rep-mov-producto')?.value || '',
                tipoMovimiento: document.getElementById('filtro-rep-mov-tipo')?.value || '',
                fechaInicio: document.getElementById('filtro-rep-mov-desde')?.value || '',
                fechaFin: document.getElementById('filtro-rep-mov-hasta')?.value || ''
            };
            const res = await ReporteService.getMovimientosFiltrados(params);
            ReporteRenderer.renderMovimientosTabla(res);
        } catch (err) {
            Toast.error('Error al filtrar movimientos');
        }
    },

    async filtrarAuditoria() {
        try {
            const params = {
                producto: document.getElementById('filtro-rep-aud-producto')?.value || '',
                campoModificado: document.getElementById('filtro-rep-aud-campo')?.value || '',
                fechaInicio: document.getElementById('filtro-rep-aud-desde')?.value || '',
                fechaFin: document.getElementById('filtro-rep-aud-hasta')?.value || ''
            };
            const res = await ReporteService.getAuditoriaFiltrada(params);
            ReporteRenderer.renderAuditoriaTabla(res);
        } catch (err) {
            Toast.error('Error al filtrar auditoría');
        }
    },


    initEventListeners() {
        document.getElementById('btn-ejecutar-filtro-mov')?.addEventListener('click', () => this.filtrarMovimientos());
        document.getElementById('btn-ejecutar-filtro-aud')?.addEventListener('click', () => this.filtrarAuditoria());
    },

    initExportButtons() {
        if (typeof ExportService === 'undefined') return;

        const btnPdfResumen = document.getElementById('btn-export-pdf-resumen');
        if (btnPdfResumen) btnPdfResumen.onclick = (e) => { e.preventDefault(); ExportService.exportarResumenPdf(); };

        const btnExcelResumen = document.getElementById('btn-export-excel-resumen');
        if (btnExcelResumen) btnExcelResumen.onclick = (e) => { e.preventDefault(); ExportService.exportarResumenExcel(); };

        const btnPdfMov = document.getElementById('btn-export-pdf-mov');
        if (btnPdfMov) btnPdfMov.onclick = (e) => { e.preventDefault(); ExportService.exportarMovimientosPdf(); };

        const btnExcelMov = document.getElementById('btn-export-excel-mov');
        if (btnExcelMov) btnExcelMov.onclick = (e) => { e.preventDefault(); ExportService.exportarMovimientosExcel(); };
    }
};

window.ReporteModuleController = ReporteModuleController;
