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
                    <h4 style="margin-bottom: 1.25rem; font-size: 1.1rem; color:var(--text-bright);">Stock Total Acumulado por Bodega</h4>
                    ${bodegasHtml || '<p style="color: var(--text-muted);">Sin datos de stock.</p>'}
                </div>

                <div class="report-card">
                    <h4 style="margin-bottom: 1.25rem; font-size: 1.1rem; color:var(--text-bright);">Top Productos Más Movidos</h4>
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
    },

    renderMovimientosTabla(movimientos) {
        const tbody = document.getElementById('tabla-rep-mov-body');
        const badgeCount = document.getElementById('contador-rep-mov');
        if (badgeCount) badgeCount.innerText = `${movimientos ? movimientos.length : 0} registros`;
        if (!tbody) return;

        if (!movimientos || movimientos.length === 0) {
            tbody.innerHTML = `<tr><td colspan="7" class="text-center py-4" style="color:var(--text-muted);">No se encontraron movimientos con los filtros seleccionados.</td></tr>`;
            return;
        }

        tbody.innerHTML = movimientos.map(m => {
            let badgeClass = 'badge-info';
            if (m.tipoMovimiento === 'ENTRADA') badgeClass = 'badge-success';
            if (m.tipoMovimiento === 'SALIDA') badgeClass = 'badge-danger';
            if (m.tipoMovimiento === 'TRANSFERENCIA') badgeClass = 'badge-warning';

            const fecha = m.fecha ? new Date(m.fecha).toLocaleString('es-CO') : '-';
            const productos = (m.detalles || []).map(d => `${d.productoNombre} (<b>${d.cantidad}u</b>)`).join(', ') || 'Sin detalle';
            const contacto = m.proveedorNombre ? m.proveedorNombre : (m.clienteNombre ? m.clienteNombre : (m.usuarioNombre || '-'));

            return `
                <tr>
                    <td style="font-family:var(--font-mono); color:var(--accent); font-weight:600;">#${m.id}</td>
                    <td style="font-size:12px; font-family:var(--font-mono);">${fecha}</td>
                    <td><span class="badge ${badgeClass}">${m.tipoMovimiento}</span></td>
                    <td><strong>${m.bodegaOrigen || '— (Entrada directa)'}</strong></td>
                    <td><strong>${m.bodegaDestino || '— (Salida directa)'}</strong></td>
                    <td><small style="color:var(--text-muted);">${contacto}</small></td>
                    <td><small style="color:var(--text-bright);">${productos}</small></td>
                </tr>
            `;
        }).join('');
    },

    renderAuditoriaTabla(auditorias) {
        const tbody = document.getElementById('tabla-rep-aud-body');
        const badgeCount = document.getElementById('contador-rep-aud');
        if (badgeCount) badgeCount.innerText = `${auditorias ? auditorias.length : 0} registros`;
        if (!tbody) return;

        if (!auditorias || auditorias.length === 0) {
            tbody.innerHTML = `<tr><td colspan="6" class="text-center py-4" style="color:var(--text-muted);">No se encontraron registros de auditoría con los filtros seleccionados.</td></tr>`;
            return;
        }

        tbody.innerHTML = auditorias.map(a => {
            let badgeClass = 'badge-info';
            if (a.tipoOperacion === 'INSERT') badgeClass = 'badge-success';
            if (a.tipoOperacion === 'UPDATE') badgeClass = 'badge-warning';
            if (a.tipoOperacion === 'DELETE') badgeClass = 'badge-danger';

            const fecha = a.fechaHora ? new Date(a.fechaHora).toLocaleString('es-CO') : '-';

            return `
                <tr>
                    <td style="font-family:var(--font-mono); color:var(--accent); font-weight:600;">#${a.id}</td>
                    <td style="font-size:12px; font-family:var(--font-mono);">${fecha}</td>
                    <td><span class="badge ${badgeClass}">${a.tipoOperacion}</span></td>
                    <td><strong>${a.recursoNombre || a.entidad}</strong> <small style="color:var(--text-dim);">(${a.entidad})</small></td>
                    <td><span style="font-family:var(--font-mono); font-size:12px; color:var(--text-bright);">${a.usuarioEmail || 'Sistema'}</span></td>
                    <td><small style="color:var(--text);">${a.descripcion || '-'}</small></td>
                </tr>
            `;
        }).join('');
    }
};

window.ReporteRenderer = ReporteRenderer;

