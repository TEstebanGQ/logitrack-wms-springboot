/* ==========================================
   LogiTrack S.A. - Dashboard UI Renderer
   ========================================== */

const DashboardRenderer = {
    renderBodegasCards(bodegas, containerId = 'dashboard-bodegas-container') {
        const container = document.getElementById(containerId);
        if (!container) return;

        if (!bodegas || bodegas.length === 0) {
            container.innerHTML = `<p style="padding: 1rem; color: var(--text-muted); grid-column:1/-1;">No hay bodegas registradas.</p>`;
            return;
        }

        const currentUser = typeof AuthService !== 'undefined' ? AuthService.getCurrentUser() : null;
        const canManage = currentUser && (currentUser.rol === 'ADMIN' || currentUser.rol === 'SUPERVISOR' || currentUser.rol === 'SUPER_ADMIN');

        const html = bodegas.slice(0, 3).map((b, idx) => {
            const capTotal = b.capacidad || 2000;
            const pcts = [82, 91, 54, 76, 60];
            const capUsadaPct = pcts[idx % pcts.length];

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
                    <div class="bodega-foot" style="display: flex; justify-content: space-between; align-items: center;">
                        <span>ENC: ${b.encargado || 'Operador'}</span>
                        <div style="display: flex; gap: 6px;">
                            <button class="btn btn-secondary btn-sm" style="padding: 2px 8px; font-size: 10px;" onclick="BodegaModuleController.verInventario(${b.id})">Inventario</button>
                            ${canManage ? `
                                <button class="btn btn-secondary btn-sm" style="padding: 2px 8px; font-size: 10px;" onclick="BodegaModuleController.edit(${b.id})">Editar</button>
                                <button class="btn btn-danger btn-sm" style="padding: 2px 8px; font-size: 10px;" onclick="BodegaModuleController.delete(${b.id})">Eliminar</button>
                            ` : ''}
                        </div>
                    </div>
                </div>
            `;
        }).join('');

        container.innerHTML = html;
    },

    renderMovimientosRecientes(movimientos, containerId = 'dashboard-movimientos-container') {
        const container = document.getElementById(containerId);
        if (!container) return;

        const list = Array.isArray(movimientos) ? movimientos : (movimientos && Array.isArray(movimientos.content) ? movimientos.content : []);

        if (list.length === 0) {
            container.innerHTML = `<p style="padding: 1rem; color: var(--text-muted);">No hay movimientos recientes.</p>`;
            return;
        }

        const rows = list.slice(0, 5).map(m => {
            let badgeClass = 'badge-info';
            let tipoText = m.tipoMovimiento;
            if (m.tipoMovimiento === 'ENTRADA')        { badgeClass = 'badge-success'; tipoText = '■ ENTRADA'; }
            if (m.tipoMovimiento === 'SALIDA')         { badgeClass = 'badge-danger';  tipoText = '■ SALIDA'; }
            if (m.tipoMovimiento === 'TRANSFERENCIA')  { badgeClass = 'badge-warning'; tipoText = '■ TRANSF.'; }

            const horaStr = m.fecha ? new Date(m.fecha).toLocaleTimeString([], {hour: '2-digit', minute:'2-digit'}) : '08:47';
            const productoNombre = (m.detalles && m.detalles.length > 0)
                ? m.detalles.map(d => d.productoNombre).join(', ')
                : (m.productoNombre || 'Producto');
            const cantidad = (m.detalles && m.detalles.length > 0)
                ? m.detalles.map(d => d.cantidad).join(', ')
                : (m.cantidad || 1);
            const bodegaStr = m.bodegaOrigen ? m.bodegaOrigen : (m.bodegaDestino ? m.bodegaDestino : 'B-01');
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

    renderBajoStockList(productos, alertasPendientes = [], containerId = 'dashboard-bajo-stock-container') {
        const container = document.getElementById(containerId);
        if (!container) return;

        // Si tenemos alertas activas desde la base de datos, mostrarlas prioritariamente
        if (alertasPendientes && alertasPendientes.length > 0) {
            const rows = alertasPendientes.slice(0, 5).map(a => `
                <div class="alert-row" style="display: flex; justify-content: space-between; align-items: center; border-left: 3px solid var(--danger); padding-left: 10px;">
                    <div>
                        <div class="alert-name"><strong>${a.productoNombre || 'Producto'}</strong></div>
                        <div class="alert-cat" style="color: var(--text-muted); font-size: 11px;">${a.bodegaNombre || 'Bodega'} · Mín: ${a.stockMinimo} u.</div>
                    </div>
                    <div style="display: flex; align-items: center; gap: 6px;">
                        <div class="alert-stock" style="margin-right: 4px; color: var(--danger); font-weight: 700;">${a.stockActual} u.</div>
                        <button class="btn btn-secondary btn-sm" style="padding: 2px 8px; font-size: 10px;" onclick="ProductoModuleController.edit(${a.productoId})" title="Editar producto y ajustar stock mínimo">Editar</button>
                        <button class="btn btn-danger btn-sm" style="padding: 2px 8px; font-size: 10px;" onclick="App.eliminarAlertaStock(${a.id})" title="Descartar / Eliminar alerta">Eliminar</button>
                    </div>
                </div>
            `).join('');
            container.innerHTML = rows;
            return;
        }

        if (!productos || productos.length === 0) {
            container.innerHTML = `<p style="padding: 1rem; color: var(--text-muted);">Sin alertas de bajo stock.</p>`;
            return;
        }

        const currentUser = typeof AuthService !== 'undefined' ? AuthService.getCurrentUser() : null;
        const canManage = currentUser && (currentUser.rol === 'ADMIN' || currentUser.rol === 'SUPERVISOR' || currentUser.rol === 'SUPER_ADMIN');

        const rows = productos.slice(0, 5).map(p => `
            <div class="alert-row" style="display: flex; justify-content: space-between; align-items: center;">
                <div>
                    <div class="alert-name"><strong>${p.nombre}</strong></div>
                    <div class="alert-cat">${p.categoriaNombre || 'General'} · Catálogo</div>
                </div>
                <div style="display: flex; align-items: center; gap: 8px;">
                    <div class="alert-stock" style="margin-right: 4px;">${p.stock} u.</div>
                    ${canManage ? `
                        <button class="btn btn-secondary btn-sm" style="padding: 3px 8px; font-size: 11px;" onclick="ProductoModuleController.edit(${p.id})">Editar</button>
                        <button class="btn btn-danger btn-sm" style="padding: 3px 8px; font-size: 11px;" onclick="ProductoModuleController.delete(${p.id})">Eliminar</button>
                    ` : ''}
                </div>
            </div>
        `).join('');

        container.innerHTML = rows;
    }
};

window.DashboardRenderer = DashboardRenderer;
