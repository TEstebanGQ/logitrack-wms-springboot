/* ==========================================
   LogiTrack S.A. - Reporte UI Renderer
   ========================================== */

const ReporteRenderer = {
    renderSection(reporte, containerId = 'reportes-list-container') {
        const container = document.getElementById(containerId);
        if (!container) return;

        if (!reporte) {
            container.innerHTML = `<p style="color: var(--text-muted);">No se pudieron cargar los datos del reporte.</p>`;
            return;
        }

        const stockBodegas = reporte.stockPorBodega || [];
        const masMovidos = reporte.productosMasMovidos || [];

        const maxStock = Math.max(...stockBodegas.map(b => b.stockTotal || 0), 1);

        const bodegasHtml = stockBodegas.map(b => {
            const pct = Math.round((b.stockTotal / maxStock) * 100);
            return `
                <div style="margin-bottom: 1rem;">
                    <div style="display: flex; justify-content: space-between; font-size: 0.875rem; margin-bottom: 0.25rem;">
                        <span><strong>${b.bodegaNombre}</strong> (ID #${b.bodegaId})</span>
                        <span class="badge badge-info">${b.stockTotal.toLocaleString()} unidades</span>
                    </div>
                    <div class="progress-bar-container">
                        <div class="progress-bar-fill" style="width: ${pct}%;"></div>
                    </div>
                </div>
            `;
        }).join('');

        const movidosRows = masMovidos.map((m, idx) => `
            <tr>
                <td>#${idx + 1}</td>
                <td><strong>${m.productoNombre}</strong> (ID #${m.productoId})</td>
                <td><span class="badge badge-success">${m.totalMovido.toLocaleString()} unidades</span></td>
            </tr>
        `).join('');

        container.innerHTML = `
            <div style="display: grid; grid-template-columns: repeat(auto-fit, minmax(320px, 1fr)); gap: 1.5rem;">
                <div class="report-card">
                    <h4 style="margin-bottom: 1.25rem; font-size: 1.1rem;">🏬 Stock Total Acumulado por Bodega</h4>
                    ${bodegasHtml || '<p style="color: var(--text-muted);">Sin datos de stock.</p>'}
                </div>

                <div class="report-card">
                    <h4 style="margin-bottom: 1.25rem; font-size: 1.1rem;">🔥 Top Productos Más Movidos</h4>
                    <div class="table-container">
                        <table class="table">
                            <thead>
                                <tr>
                                    <th>#</th>
                                    <th>Producto</th>
                                    <th>Total Movido</th>
                                </tr>
                            </thead>
                            <tbody>
                                ${movidosRows || '<tr><td colspan="3" style="color: var(--text-muted);">Sin movimientos registrados.</td></tr>'}
                            </tbody>
                        </table>
                    </div>
                </div>
            </div>
        `;
    }
};

window.ReporteRenderer = ReporteRenderer;
