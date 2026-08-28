/* ==========================================
   LogiTrack S.A. - Reporte Controller Module
   ========================================== */

const ReporteModuleController = {
    async load() {
        try {
            const reporte = await ReporteService.getResumenGeneral().catch(() => null);
            ReporteRenderer.renderSection(reporte);
            this.initExportButtons();
        } catch (err) {
            console.error('Error cargando reportes:', err);
            Toast.error('Error al cargar reporte general');
        }
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
