// export.service.js - Descarga de archivos desde la API

const ExportService = {
  async descargar(url, filename) {
    try {
      const token = ApiService.getToken();
      const resp = await fetch(url, {
        headers: { 'Authorization': `Bearer ${token}` }
      });
      if (!resp.ok) throw new Error('Error al generar el archivo');
      const blob = await resp.blob();
      const link = document.createElement('a');
      link.href = URL.createObjectURL(blob);
      link.download = filename;
      link.click();
      URL.revokeObjectURL(link.href);
    } catch (e) {
      Toast.error('Error al exportar: ' + e.message);
    }
  },

  exportarResumenExcel()    { return this.descargar('/api/reportes/exportar/excel', 'logitrack-resumen.xlsx'); },
  exportarResumenPdf()      { return this.descargar('/api/reportes/exportar/pdf', 'logitrack-resumen.pdf'); },
  exportarMovimientosExcel() {
    const hace30 = new Date(); hace30.setDate(hace30.getDate() - 30);
    const desde = hace30.toISOString().split('T')[0];
    const hasta = new Date().toISOString().split('T')[0];
    return this.descargar(`/api/reportes/movimientos/exportar/excel?desde=${desde}&hasta=${hasta}`, 'logitrack-movimientos.xlsx');
  },
  exportarMovimientosPdf() {
    const hace30 = new Date(); hace30.setDate(hace30.getDate() - 30);
    const desde = hace30.toISOString().split('T')[0];
    const hasta = new Date().toISOString().split('T')[0];
    return this.descargar(`/api/reportes/movimientos/exportar/pdf?desde=${desde}&hasta=${hasta}`, 'logitrack-movimientos.pdf');
  }
};
