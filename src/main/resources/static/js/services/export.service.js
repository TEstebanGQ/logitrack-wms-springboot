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
      Toast.success(`Archivo '${filename}' generado con éxito`);
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
  },
  exportarAuditoriasExcel() { return this.descargar('/api/reportes/auditorias/exportar/excel', 'logitrack-auditoria.xlsx'); },
  exportarAuditoriasPdf()   { return this.descargar('/api/reportes/auditorias/exportar/pdf', 'logitrack-auditoria.pdf'); },

  exportarCSV(datos, encabezados, nombreArchivo) {
    if (!datos || !datos.length) {
      Toast.error('No hay datos disponibles para exportar');
      return;
    }
    const lineas = [];
    lineas.push(encabezados.map(h => `"${h.label}"`).join(','));

    datos.forEach(item => {
      const fila = encabezados.map(h => {
        let val = typeof h.key === 'function' ? h.key(item) : item[h.key];
        if (val === null || val === undefined) val = '';
        return `"${String(val).replace(/"/g, '""')}"`;
      });
      lineas.push(fila.join(','));
    });

    const csvContent = 'data:text/csv;charset=utf-8,\uFEFF' + encodeURIComponent(lineas.join('\n'));
    const link = document.createElement('a');
    link.href = csvContent;
    link.download = nombreArchivo;
    link.click();
    Toast.success(`Exportación Excel/CSV '${nombreArchivo}' completada`);
  },

  exportarPDFTabular(titulo, encabezados, datos, nombreArchivo) {
    if (!datos || !datos.length) {
      Toast.error('No hay datos disponibles para exportar a PDF');
      return;
    }

    const ventana = window.open('', '_blank');
    if (!ventana) {
      Toast.error('Por favor permite ventanas emergentes para generar el PDF');
      return;
    }

    const filasHtml = datos.map(item => {
      const celdas = encabezados.map(h => {
        let val = typeof h.key === 'function' ? h.key(item) : item[h.key];
        if (val === null || val === undefined) val = '';
        return `<td style="padding: 7px 10px; border-bottom: 1px solid #e2e8f0; font-size: 11px;">${val}</td>`;
      }).join('');
      return `<tr>${celdas}</tr>`;
    }).join('');

    const headersHtml = encabezados.map(h => `<th style="background: #0f172a; color: #f8fafc; padding: 8px 10px; text-align: left; font-size: 11px; text-transform: uppercase;">${h.label}</th>`).join('');

    const html = `
      <!DOCTYPE html>
      <html>
      <head>
        <meta charset="utf-8">
        <title>${titulo} - LogiTrack S.A.</title>
        <style>
          body { font-family: -apple-system, BlinkMacSystemFont, "Segoe UI", Roboto, Arial, sans-serif; margin: 24px; color: #1e293b; }
          .header { display: flex; justify-content: space-between; align-items: center; border-bottom: 2px solid #FF6A2B; padding-bottom: 12px; margin-bottom: 16px; }
          .title h2 { margin: 0; color: #0f172a; font-size: 18px; }
          .title p { margin: 4px 0 0 0; font-size: 11px; color: #64748b; }
          .meta { text-align: right; font-size: 11px; color: #64748b; font-family: monospace; }
          table { width: 100%; border-collapse: collapse; margin-top: 10px; }
          tr:nth-child(even) { background-color: #f8fafc; }
          @media print {
            body { margin: 0; }
            @page { size: landscape; margin: 15mm; }
          }
        </style>
      </head>
      <body>
        <div class="header">
          <div class="title">
            <h2>LOGITRACK S.A. &mdash; ${titulo}</h2>
            <p>Reporte Oficial de Auditoría y Trazabilidad de Inventario WMS</p>
          </div>
          <div class="meta">
            <div>Fecha: ${new Date().toLocaleString()}</div>
            <div>Registros: ${datos.length}</div>
          </div>
        </div>
        <table>
          <thead>
            <tr>${headersHtml}</tr>
          </thead>
          <tbody>
            ${filasHtml}
          </tbody>
        </table>
        <script>
          window.onload = function() {
            setTimeout(function() {
              window.print();
            }, 300);
          };
        </script>
      </body>
      </html>
    `;

    ventana.document.open();
    ventana.document.write(html);
    ventana.document.close();
    Toast.success(`Vista de impresión / PDF generada para ${titulo}`);
  }
};

window.ExportService = ExportService;

