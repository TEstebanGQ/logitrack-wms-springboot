/* ==========================================
   LogiTrack S.A. - UI DOM Renderers
   ========================================== */

const Renderers = {
    renderBodegasTable(bodegas, containerId) {
        const container = document.getElementById(containerId);
        if (!container) return;

        if (!bodegas || bodegas.length === 0) {
            container.innerHTML = `<p style="padding: 1rem; color: var(--text-muted);">No hay bodegas registradas.</p>`;
            return;
        }

        const rows = bodegas.map(b => `
            <tr>
                <td>#${b.id}</td>
                <td><strong>${b.nombre}</strong></td>
                <td>${b.ubicacion}</td>
                <td>${b.capacidad.toLocaleString()} unidades</td>
                <td>${b.encargado}</td>
                <td><span class="badge ${b.activo ? 'badge-success' : 'badge-danger'}">${b.activo ? 'Activo' : 'Inactivo'}</span></td>
                <td>
                    <button class="btn btn-secondary btn-sm" onclick="App.editBodega(${b.id})">Editar</button>
                    <button class="btn btn-danger btn-sm" onclick="App.deleteBodega(${b.id})">Eliminar</button>
                </td>
            </tr>
        `).join('');

        container.innerHTML = `
            <div class="table-container">
                <table class="table">
                    <thead>
                        <tr>
                            <th>ID</th>
                            <th>Nombre</th>
                            <th>Ubicación</th>
                            <th>Capacidad</th>
                            <th>Encargado</th>
                            <th>Estado</th>
                            <th>Acciones</th>
                        </tr>
                    </thead>
                    <tbody>${rows}</tbody>
                </table>
            </div>
        `;
    },

    renderProductosTable(productos, containerId) {
        const container = document.getElementById(containerId);
        if (!container) return;

        if (!productos || productos.length === 0) {
            container.innerHTML = `<p style="padding: 1rem; color: var(--text-muted);">No hay productos registrados.</p>`;
            return;
        }

        const rows = productos.map(p => {
            const lowStock = p.stock < 10;
            return `
                <tr>
                    <td>#${p.id}</td>
                    <td><strong>${p.nombre}</strong></td>
                    <td><span class="badge badge-info">${p.categoriaNombre || p.categoria || 'General'}</span></td>
                    <td>
                        <span class="badge ${lowStock ? 'badge-danger' : 'badge-success'}">
                            ${p.stock} unidades ${lowStock ? '⚠️ Bajo Stock' : ''}
                        </span>
                    </td>
                    <td>$${Number(p.precio).toLocaleString('es-CO')}</td>
                    <td>${p.descripcion || '-'}</td>
                    <td>
                        <button class="btn btn-secondary btn-sm" onclick="App.editProducto(${p.id})">Editar</button>
                        <button class="btn btn-danger btn-sm" onclick="App.deleteProducto(${p.id})">Eliminar</button>
                    </td>
                </tr>
            `;
        }).join('');

        container.innerHTML = `
            <div class="table-container">
                <table class="table">
                    <thead>
                        <tr>
                            <th>ID</th>
                            <th>Nombre</th>
                            <th>Categoría</th>
                            <th>Stock</th>
                            <th>Precio</th>
                            <th>Descripción</th>
                            <th>Acciones</th>
                        </tr>
                    </thead>
                    <tbody>${rows}</tbody>
                </table>
            </div>
        `;
    },

    renderMovimientosTable(movimientos, containerId) {
        const container = document.getElementById(containerId);
        if (!container) return;

        if (!movimientos || movimientos.length === 0) {
            container.innerHTML = `<p style="padding: 1rem; color: var(--text-muted);">No hay movimientos registrados.</p>`;
            return;
        }

        const rows = movimientos.map(m => {
            let badgeClass = 'badge-info';
            if (m.tipoMovimiento === 'ENTRADA') badgeClass = 'badge-success';
            if (m.tipoMovimiento === 'SALIDA') badgeClass = 'badge-danger';
            if (m.tipoMovimiento === 'TRANSFERENCIA') badgeClass = 'badge-warning';

            const fecha = new Date(m.fecha).toLocaleString();

            return `
                <tr>
                    <td>#${m.id}</td>
                    <td><span class="badge ${badgeClass}">${m.tipoMovimiento}</span></td>
                    <td>${fecha}</td>
                    <td>${m.usuarioNombre || 'Usuario #' + m.usuarioId}</td>
                    <td>${m.bodegaOrigenNombre || '-'}</td>
                    <td>${m.bodegaDestinoNombre || '-'}</td>
                    <td>${m.observaciones || '-'}</td>
                </tr>
            `;
        }).join('');

        container.innerHTML = `
            <div class="table-container">
                <table class="table">
                    <thead>
                        <tr>
                            <th>ID</th>
                            <th>Tipo</th>
                            <th>Fecha</th>
                            <th>Usuario</th>
                            <th>Origen</th>
                            <th>Destino</th>
                            <th>Observaciones</th>
                        </tr>
                    </thead>
                    <tbody>${rows}</tbody>
                </table>
            </div>
        `;
    },

    renderAuditoriasTable(auditorias, containerId) {
        const container = document.getElementById(containerId);
        if (!container) return;

        if (!auditorias || auditorias.length === 0) {
            container.innerHTML = `<p style="padding: 1rem; color: var(--text-muted);">No hay registros de auditoría.</p>`;
            return;
        }

        window.auditoriaDataStore = auditorias;

        const rows = auditorias.map(a => {
            let badgeClass = 'badge-info';
            if (a.tipoOperacion === 'INSERT') badgeClass = 'badge-success';
            if (a.tipoOperacion === 'DELETE') badgeClass = 'badge-danger';
            if (a.tipoOperacion === 'UPDATE') badgeClass = 'badge-warning';

            const fecha = new Date(a.fechaHora).toLocaleString();

            return `
                <tr>
                    <td>#${a.id}</td>
                    <td><strong>${a.entidad}</strong> (#${a.entidadId || '-'})</td>
                    <td><span class="badge ${badgeClass}">${a.tipoOperacion}</span></td>
                    <td>${fecha}</td>
                    <td>${a.usuarioId ? 'Usuario #' + a.usuarioId : 'Sistema'}</td>
                    <td><small style="color: var(--text-muted);">${a.descripcion || '-'}</small></td>
                    <td>
                        <button class="btn btn-secondary btn-sm" onclick="App.inspectAuditoria(${a.id})">👁️ Ver Detalle</button>
                    </td>
                </tr>
            `;
        }).join('');

        container.innerHTML = `
            <div class="table-container">
                <table class="table">
                    <thead>
                        <tr>
                            <th>ID</th>
                            <th>Entidad Afectada</th>
                            <th>Operación</th>
                            <th>Fecha/Hora</th>
                            <th>Usuario</th>
                            <th>Descripción</th>
                            <th>Acción</th>
                        </tr>
                    </thead>
                    <tbody>${rows}</tbody>
                </table>
            </div>
        `;
    },

    renderReportesSection(reporte, containerId) {
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
    },

    renderDashboardBodegasCards(bodegas, containerId) {
        const container = document.getElementById(containerId);
        if (!container) return;

        if (!bodegas || bodegas.length === 0) {
            container.innerHTML = `<p style="padding: 1rem; color: var(--text-muted); grid-column:1/-1;">No hay bodegas registradas.</p>`;
            return;
        }

        const html = bodegas.slice(0, 3).map((b, idx) => {
            const capTotal = b.capacidad || 2000;
            const pcts = [82, 91, 54, 76, 60];
            const capUsadaPct = pcts[idx % pcts.length];
            const unidadesUsadas = Math.round((capTotal * capUsadaPct) / 100);

            return `
                <div class="bodega-card">
                    <div class="bodega-head">
                        <div>
                            <div class="bodega-name">${b.nombre}</div>
                            <div class="bodega-loc">${b.ubicacion || 'Colombia'}</div>
                        </div>
                        <div class="bodega-id">B 0${b.id}</div>
                    </div>
                    <div class="gauge-label">
                        <span>Capacidad usada</span>
                        <span>${capUsadaPct}%</span>
                    </div>
                    <div class="gauge">
                        <div class="gauge-fill ${capUsadaPct > 85 ? 'warn' : ''}" style="width: ${capUsadaPct}%"></div>
                    </div>
                    <div class="bodega-foot">
                        <span>ENC: ${b.encargado || 'Operador'}</span>
                        <span>${unidadesUsadas.toLocaleString()} / ${capTotal.toLocaleString()}</span>
                    </div>
                </div>
            `;
        }).join('');

        container.innerHTML = html;
    },

    renderDashboardMovimientosRecientes(movimientos, containerId) {
        const container = document.getElementById(containerId);
        if (!container) return;

        if (!movimientos || movimientos.length === 0) {
            container.innerHTML = `<p style="padding: 1rem; color: var(--text-muted);">No hay movimientos recientes.</p>`;
            return;
        }

        const rows = movimientos.slice(0, 5).map(m => {
            let badgeClass = 'badge-info';
            let tipoText = m.tipoMovimiento;
            if (m.tipoMovimiento === 'ENTRADA')        { badgeClass = 'badge-success'; tipoText = '■ ENTRADA'; }
            if (m.tipoMovimiento === 'SALIDA')         { badgeClass = 'badge-danger';  tipoText = '■ SALIDA'; }
            if (m.tipoMovimiento === 'TRANSFERENCIA')  { badgeClass = 'badge-warning'; tipoText = '■ TRANSF.'; }

            const horaStr = m.fecha ? new Date(m.fecha).toLocaleTimeString([], {hour: '2-digit', minute:'2-digit'}) : '08:47';
            const productoNombre = (m.detalles && m.detalles.length > 0) ? m.detalles[0].productoNombre : 'Producto';
            const cantidad = (m.detalles && m.detalles.length > 0) ? m.detalles[0].cantidad : 10;
            const bodegaStr = m.bodegaOrigenNombre ? `B-0${m.bodegaOrigenId}` : (m.bodegaDestinoNombre ? `B-0${m.bodegaDestinoId}` : 'B-01');
            const usuarioStr = m.usuarioNombre ? m.usuarioNombre.toLowerCase().split(' ')[0] : 'operador';

            return `
                <tr>
                    <td><span class="badge ${badgeClass}">${tipoText}</span></td>
                    <td><strong>${productoNombre}</strong></td>
                    <td>${cantidad}</td>
                    <td><span style="font-family:var(--font-mono);">${bodegaStr}</span></td>
                    <td><small style="color:var(--text-muted);">${usuarioStr}</small></td>
                    <td><small style="font-family:var(--font-mono); color:var(--text-dim);">${horaStr}</small></td>
                </tr>
            `;
        }).join('');

        container.innerHTML = `
            <table>
                <thead>
                    <tr>
                        <th>TIPO</th>
                        <th>PRODUCTO</th>
                        <th>CANT.</th>
                        <th>BODEGA</th>
                        <th>USUARIO</th>
                        <th>HORA</th>
                    </tr>
                </thead>
                <tbody>${rows}</tbody>
            </table>
        `;
    },

    renderDashboardBajoStockList(productos, containerId) {
        const container = document.getElementById(containerId);
        if (!container) return;

        if (!productos || productos.length === 0) {
            container.innerHTML = `<p style="padding: 1rem; color: var(--text-muted);">Sin alertas de bajo stock.</p>`;
            return;
        }

        const rows = productos.slice(0, 5).map(p => `
            <div class="alert-row">
                <div>
                    <div class="alert-name"><strong>${p.nombre}</strong></div>
                    <div class="alert-cat">${p.categoriaNombre || 'General'} · B-01</div>
                </div>
                <div class="alert-stock">${p.stock} u.</div>
            </div>
        `).join('');

        container.innerHTML = rows;
    }
};

window.Renderers = Renderers;
