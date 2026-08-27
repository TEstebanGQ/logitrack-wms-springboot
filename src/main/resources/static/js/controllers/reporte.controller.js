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
        const map = {
            'btn-export-excel-resumen': () => ExportService.exportarResumenExcel(),
            'btn-export-pdf-resumen':   () => ExportService.exportarResumenPdf(),
            'btn-export-excel-mov':     () => ExportService.exportarMovimientosExcel(),
            'btn-export-pdf-mov':       () => ExportService.exportarMovimientosPdf(),
        };
        Object.entries(map).forEach(([id, fn]) => {
            const btn = document.getElementById(id);
            if (btn) {
                btn.removeEventListener('click', fn);
                btn.addEventListener('click', fn);
            }
        });
    }
};

window.ReporteModuleController = ReporteModuleController;
