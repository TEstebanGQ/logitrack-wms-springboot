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
                    <h4 style="margin-bottom: 1.25rem; font-size: 1.1rem; color:var(--text-bright);">🏬 Stock Total Acumulado por Bodega</h4>
                    ${bodegasHtml || '<p style="color: var(--text-muted);">Sin datos de stock.</p>'}
                </div>

                <div class="report-card">
                    <h4 style="margin-bottom: 1.25rem; font-size: 1.1rem; color:var(--text-bright);">🔥 Top Productos Más Movidos</h4>
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

            const fecha = m.fecha ? new Date(m.fecha).toLocaleString() : '-';
            const productos = (m.detalles || []).map(d => `${d.productoNombre} (<b>${d.cantidad}u</b>)`).join(', ') || 'Sin detalle';
            const contacto = m.proveedorNombre ? `🏢 ${m.proveedorNombre}` : (m.clienteNombre ? `👤 ${m.clienteNombre}` : (m.usuarioNombre || '-'));

            return `
                <tr>
                    <td style="font-family:var(--font-mono); color:var(--accent);">#${m.id}</td>
                    <td style="font-size:12px;">${fecha}</td>
                    <td><span class="badge ${badgeClass}">${m.tipoMovimiento}</span></td>
                    <td>${m.bodegaOrigen || '— (Entrada directa)'}</td>
                    <td>${m.bodegaDestino || '— (Salida directa)'}</td>
                    <td><small style="color:var(--text-muted);">${contacto}</small></td>
                    <td><small style="color:var(--text);">${productos}</small></td>
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

            const fecha = a.fechaHora ? new Date(a.fechaHora).toLocaleString() : '-';

            return `
                <tr>
                    <td style="font-family:var(--font-mono); color:var(--accent);">#${a.id}</td>
                    <td style="font-size:12px;">${fecha}</td>
                    <td><span class="badge ${badgeClass}">${a.tipoOperacion}</span></td>
                    <td><strong>${a.recursoNombre || a.entidad}</strong> <small style="color:var(--text-dim);">(${a.entidad})</small></td>
                    <td><small style="color:var(--text-muted);">${a.usuarioEmail || 'Sistema'}</small></td>
                    <td><small style="color:var(--text);">${a.descripcion || '-'}</small></td>
                </tr>
            `;
        }).join('');
    },

    renderMatrizAbc(abcData) {
        const container = document.getElementById('abc-container');
        if (!container) return;

        if (!abcData) {
            container.innerHTML = `<p style="color:var(--danger); text-align:center;">Error al cargar la clasificación ABC.</p>`;
            return;
        }

        const renderTableItems = (items, badgeClass) => {
            if (!items || items.length === 0) {
                return `<tr><td colspan="5" style="color:var(--text-muted); text-align:center;">Sin productos en este segmento</td></tr>`;
            }
            return items.map(it => `
                <tr>
                    <td><strong>${it.productoNombre}</strong></td>
                    <td><small style="color:var(--text-muted);">${it.categoriaNombre}</small></td>
                    <td>${it.stock} u.</td>
                    <td>$${it.valorValorizado ? it.valorValorizado.toLocaleString('es-CO') : 0}</td>
                    <td><span class="badge ${badgeClass}">${it.porcentajeValor}%</span></td>
                </tr>
            `).join('');
        };

        container.innerHTML = `
            <div style="display:grid; grid-template-columns:repeat(auto-fit, minmax(240px, 1fr)); gap:16px; margin-bottom:20px;">
                <div class="card" style="padding:16px; border-left:4px solid #3b82f6;">
                    <div style="font-size:12px; color:var(--text-muted); text-transform:uppercase;">Valor Total Inventario</div>
                    <div style="font-size:22px; font-weight:700; color:var(--text-bright); margin-top:4px;">
                        $${abcData.valorTotalInventario ? abcData.valorTotalInventario.toLocaleString('es-CO') : 0}
                    </div>
                    <small style="color:var(--text-dim);">${abcData.totalProductos} productos activos valorizados</small>
                </div>
                <div class="card" style="padding:16px; border-left:4px solid #60a5fa;">
                    <div style="font-size:12px; color:var(--text-muted); text-transform:uppercase;">Clase A (Alta Inversión - 80%)</div>
                    <div style="font-size:22px; font-weight:700; color:#60a5fa; margin-top:4px;">${abcData.itemsA ? abcData.itemsA.length : 0} productos</div>
                    <small style="color:var(--text-dim);">Representan el mayor valor financiero</small>
                </div>
                <div class="card" style="padding:16px; border-left:4px solid #c084fc;">
                    <div style="font-size:12px; color:var(--text-muted); text-transform:uppercase;">Clase B (Inversión Media - 15%)</div>
                    <div style="font-size:22px; font-weight:700; color:#c084fc; margin-top:4px;">${abcData.itemsB ? abcData.itemsB.length : 0} productos</div>
                    <small style="color:var(--text-dim);">Rotación e inversión intermedia</small>
                </div>
                <div class="card" style="padding:16px; border-left:4px solid #94a3b8;">
                    <div style="font-size:12px; color:var(--text-muted); text-transform:uppercase;">Clase C (Baja Inversión - 5%)</div>
                    <div style="font-size:22px; font-weight:700; color:#94a3b8; margin-top:4px;">${abcData.itemsC ? abcData.itemsC.length : 0} productos</div>
                    <small style="color:var(--text-dim);">Alta cantidad, menor peso financiero</small>
                </div>
            </div>

            <div style="display:grid; grid-template-columns:1fr; gap:20px;">
                <div class="card" style="padding:16px;">
                    <h4 style="margin:0 0 12px 0; color:#60a5fa;">🔵 Productos Clase A (Top 80% Valor)</h4>
                    <table class="data-table">
                        <thead><tr><th>Producto</th><th>Categoría</th><th>Stock</th><th>Valor Total</th><th>% Ponderado</th></tr></thead>
                        <tbody>${renderTableItems(abcData.itemsA, 'badge-abc-a')}</tbody>
                    </table>
                </div>

                <div class="card" style="padding:16px;">
                    <h4 style="margin:0 0 12px 0; color:#c084fc;">🟣 Productos Clase B (Siguiente 15% Valor)</h4>
                    <table class="data-table">
                        <thead><tr><th>Producto</th><th>Categoría</th><th>Stock</th><th>Valor Total</th><th>% Ponderado</th></tr></thead>
                        <tbody>${renderTableItems(abcData.itemsB, 'badge-abc-b')}</tbody>
                    </table>
                </div>

                <div class="card" style="padding:16px;">
                    <h4 style="margin:0 0 12px 0; color:#94a3b8;">⚪ Productos Clase C (Restante 5% Valor)</h4>
                    <table class="data-table">
                        <thead><tr><th>Producto</th><th>Categoría</th><th>Stock</th><th>Valor Total</th><th>% Ponderado</th></tr></thead>
                        <tbody>${renderTableItems(abcData.itemsC, 'badge-abc-c')}</tbody>
                    </table>
                </div>
            </div>
        `;
    }
};

window.ReporteRenderer = ReporteRenderer;
