/* ==========================================
   LogiTrack S.A. - Picking UI Renderer
   ========================================== */

const PickingRenderer = {
    renderTable(tareas) {
        const tbody = document.getElementById('tabla-picking-body');
        const badgeCount = document.getElementById('contador-picking');
        if (badgeCount) badgeCount.innerText = `${tareas ? tareas.length : 0} tareas`;
        if (!tbody) return;

        if (!tareas || tareas.length === 0) {
            tbody.innerHTML = `<tr><td colspan="8" class="text-center py-4" style="color:var(--text-muted);">No hay tareas de picking pendientes.</td></tr>`;
            return;
        }

        tbody.innerHTML = tareas.map(t => {
            let badgeClass = 'badge-warning';
            if (t.estado === 'EN_PROCESO') badgeClass = 'badge-info';
            if (t.estado === 'COMPLETADA') badgeClass = 'badge-success';
            if (t.estado === 'CANCELADA') badgeClass = 'badge-danger';

            const pct = Math.min(100, Math.round((t.cantidadRecogida / t.cantidadRequerida) * 100));

            const currentUser = typeof AuthService !== 'undefined' ? AuthService.getCurrentUser() : null;
            const canPick = currentUser && (currentUser.rol === 'ADMIN' || currentUser.rol === 'SUPERVISOR' || currentUser.rol === 'EMPLEADO');

            return `
                <tr>
                    <td style="font-family:var(--font-mono); color:var(--accent); font-weight:600;">${t.codigoTarea}</td>
                    <td><strong>${t.codigoPedido || 'PED-' + t.pedidoId}</strong></td>
                    <td>${t.productoNombre}</td>
                    <td><span class="badge badge-info">${t.codigoUbicacion || 'Estante General'}</span></td>
                    <td>${t.cantidadRequerida} u.</td>
                    <td>
                        <div><strong>${t.cantidadRecogida}</strong> / ${t.cantidadRequerida} u.</div>
                        <div class="progress-bar-container" style="height:4px; margin-top:2px;">
                            <div class="progress-bar-fill" style="width:${pct}%;"></div>
                        </div>
                    </td>
                    <td><span class="badge ${badgeClass}">${t.estado}</span></td>
                    <td>
                        ${(t.estado !== 'COMPLETADA' && t.estado !== 'CANCELADA' && canPick) ? `
                            <button class="btn btn-sm btn-primary" onclick="PickingController.abrirModalRecoleccion(${t.id}, '${t.productoNombre}', ${t.cantidadRequerida}, ${t.cantidadRecogida})">
                                Recolectar
                            </button>
                        ` : (t.estado === 'COMPLETADA' || t.estado === 'CANCELADA') ? '<span style="color:var(--text-muted); font-size:12px;">Finalizada</span>' : '<span style="color:var(--text-muted); font-size:12px;">Lectura</span>'}
                    </td>
                </tr>
            `;

        }).join('');
    }
};

window.PickingRenderer = PickingRenderer;
