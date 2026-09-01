/* ==========================================
   LogiTrack S.A. - Conteo Cíclico UI Renderer
   ========================================== */

const ConteoRenderer = {
    renderTable(conteos) {
        const tbody = document.getElementById('tabla-conteos-body');
        const badgeCount = document.getElementById('contador-conteos');
        if (badgeCount) badgeCount.innerText = `${conteos ? conteos.length : 0} auditorías`;
        if (!tbody) return;

        if (!conteos || conteos.length === 0) {
            tbody.innerHTML = `<tr><td colspan="7" class="text-center py-4" style="color:var(--text-muted);">No hay auditorías físicas registradas.</td></tr>`;
            return;
        }

        tbody.innerHTML = conteos.map(c => {
            let badgeClass = 'badge-warning';
            if (c.estado === 'EN_PROCESO') badgeClass = 'badge-info';
            if (c.estado === 'CONCILIADO' || c.estado === 'CERRADO') badgeClass = 'badge-success';

            const fecha = c.fechaProgramada || '-';
            const lineas = (c.detalles || []).length;

            return `
                <tr>
                    <td style="font-family:var(--font-mono); color:var(--accent); font-weight:600;">${c.codigoConteo}</td>
                    <td><strong>${c.bodegaNombre}</strong> <small style="color:var(--text-muted);">${c.zonaNombre ? '(' + c.zonaNombre + ')' : ''}</small></td>
                    <td>${fecha}</td>
                    <td><small>${c.supervisorNombre || 'Supervisor'}</small></td>
                    <td><span class="badge badge-info">${lineas} productos</span></td>
                    <td><span class="badge ${badgeClass}">${c.estado}</span></td>
                    <td>
                        <button class="btn btn-sm btn-primary" onclick="ConteoController.verDetalles(${c.id})">Inspeccionar / Contar</button>
                    </td>
                </tr>
            `;
        }).join('');
    },

    renderDetallesModal(conteo) {
        const container = document.getElementById('modal-conteo-lineas-container');
        const badge = document.getElementById('conteo-status-badge');
        const btnConciliar = document.getElementById('btn-conciliar-conteo');
        if (!container) return;

        if (badge) {
            badge.innerHTML = `<span class="badge badge-info">${conteo.estado}</span> &nbsp; <small style="color:var(--text-muted);">${conteo.codigoConteo} - ${conteo.bodegaNombre}</small>`;
        }

        const currentUser = typeof AuthService !== 'undefined' ? AuthService.getCurrentUser() : null;
        const canConciliar = currentUser && (currentUser.rol === 'ADMIN' || currentUser.rol === 'SUPERVISOR' || currentUser.rol === 'SUPER_ADMIN');

        if (btnConciliar) {
            btnConciliar.style.display = (conteo.estado !== 'CERRADO' && canConciliar) ? 'inline-block' : 'none';
        }


        const detalles = conteo.detalles || [];
        if (detalles.length === 0) {
            container.innerHTML = `<p style="color:var(--text-muted);">Sin productos configurados en esta auditoría.</p>`;
            return;
        }

        const rows = detalles.map(d => {
            const diffClass = d.diferencia === 0 ? 'color:var(--success);' : (d.diferencia < 0 ? 'color:var(--danger); font-weight:700;' : 'color:var(--warning); font-weight:700;');
            const isClosed = conteo.estado === 'CERRADO';

            const canCount = currentUser && (currentUser.rol === 'ADMIN' || currentUser.rol === 'SUPERVISOR' || currentUser.rol === 'EMPLEADO' || currentUser.rol === 'SUPER_ADMIN');

            return `
                <tr>
                    <td><strong style="color:var(--text-bright); font-size:13.5px;">${d.productoNombre}</strong></td>
                    <td style="text-align:center;"><span class="badge badge-info">${d.codigoUbicacion || 'Almacén General'}</span></td>
                    <td style="text-align:center;"><strong style="font-family:var(--font-mono);">${d.stockSistema}</strong> u.</td>
                    <td style="text-align:center;">
                        ${(!isClosed && canCount) ? `
                            <input type="number" class="form-control" style="width:90px; display:inline-block; text-align:center; padding:4px 8px;" value="${d.stockFisico !== null ? d.stockFisico : d.stockSistema}" min="0" onchange="ConteoController.actualizarConteoFisico(${conteo.id}, ${d.id}, this.value)">
                        ` : `<strong style="font-family:var(--font-mono);">${d.stockFisico !== null ? d.stockFisico : '-'}</strong> u.`}
                    </td>

                    <td style="text-align:center; ${diffClass}">${d.diferencia > 0 ? '+' : ''}${d.diferencia} u.</td>
                    <td style="text-align:center;"><span class="badge ${d.diferencia === 0 ? 'badge-success' : 'badge-danger'}">${d.estadoLinea}</span></td>
                </tr>
            `;
        }).join('');

        container.innerHTML = `
            <div class="table-container" style="margin-top:14px; border-radius:8px; border:1px solid var(--border-color); overflow-x:auto;">
                <table class="data-table" style="width:100%; border-collapse:collapse;">
                    <thead>
                        <tr>
                            <th>PRODUCTO</th>
                            <th style="text-align:center;">UBICACIÓN</th>
                            <th style="text-align:center;">STOCK TEÓRICO</th>
                            <th style="text-align:center;">CONTEO REAL</th>
                            <th style="text-align:center;">DIFERENCIA</th>
                            <th style="text-align:center;">ESTADO LÍNEA</th>
                        </tr>
                    </thead>
                    <tbody>${rows}</tbody>
                </table>
            </div>
        `;
    }
};

window.ConteoRenderer = ConteoRenderer;
