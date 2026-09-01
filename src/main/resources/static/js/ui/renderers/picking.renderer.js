/* ==========================================
   LogiTrack S.A. - Picking UI Renderer
   ========================================== */

const PickingRenderer = {
    renderTable(tareas) {
        const list = Array.isArray(tareas) ? tareas : (tareas && Array.isArray(tareas.content) ? tareas.content : []);
        
        const activas = list.filter(t => t.estado !== 'COMPLETADA' && t.estado !== 'CANCELADA');
        const historial = list.filter(t => t.estado === 'COMPLETADA' || t.estado === 'CANCELADA');

        // Contadores
        const badgeActivas = document.getElementById('contador-picking-activas');
        if (badgeActivas) badgeActivas.innerText = `${activas.length} pendientes`;

        const badgeHistorial = document.getElementById('contador-picking-historial');
        if (badgeHistorial) badgeHistorial.innerText = `${historial.length} finalizadas`;

        const currentUser = typeof AuthService !== 'undefined' ? AuthService.getCurrentUser() : null;
        const canPick = currentUser && (currentUser.rol === 'ADMIN' || currentUser.rol === 'SUPERVISOR' || currentUser.rol === 'EMPLEADO' || currentUser.rol === 'SUPER_ADMIN');

        // 1. Render Activas
        const tbodyActivas = document.getElementById('tabla-picking-activas-body');
        if (tbodyActivas) {
            if (activas.length === 0) {
                tbodyActivas.innerHTML = `<tr><td colspan="8" class="text-center py-4" style="color:var(--text-muted);">No hay órdenes de extracción pendientes de recolección.</td></tr>`;
            } else {
                tbodyActivas.innerHTML = activas.map(t => {
                    let badgeClass = (t.estado === 'EN_PROCESO') ? 'badge-info' : 'badge-warning';
                    const pct = Math.min(100, Math.round((t.cantidadRecogida / t.cantidadRequerida) * 100));

                    return `
                        <tr>
                            <td style="font-family:var(--font-mono); color:var(--accent); font-weight:600;">${t.codigoTarea}</td>
                            <td><strong style="color:var(--text-bright); font-family:var(--font-mono);">${t.codigoPedido || 'PED-' + t.pedidoId}</strong></td>
                            <td><strong>${t.productoNombre}</strong></td>
                            <td><span class="badge badge-info">${t.codigoUbicacion || 'Estante General'}</span></td>
                            <td><strong>${t.cantidadRequerida}</strong> u.</td>
                            <td>
                                <div><strong>${t.cantidadRecogida}</strong> / ${t.cantidadRequerida} u.</div>
                                <div class="progress-bar-container" style="height:4px; margin-top:2px;">
                                    <div class="progress-bar-fill" style="width:${pct}%;"></div>
                                </div>
                            </td>
                            <td><span class="badge ${badgeClass}">${t.estado}</span></td>
                            <td>
                                ${canPick ? `
                                    <button class="btn btn-sm btn-primary" onclick="PickingController.abrirModalRecoleccion(${t.id}, '${t.productoNombre}', ${t.cantidadRequerida}, ${t.cantidadRecogida})">
                                        Recolectar
                                    </button>
                                ` : '<span style="color:var(--text-muted); font-size:12px;">Lectura</span>'}
                            </td>
                        </tr>
                    `;
                }).join('');
            }
        }

        // 2. Render Historial
        const tbodyHistorial = document.getElementById('tabla-picking-historial-body');
        if (tbodyHistorial) {
            if (historial.length === 0) {
                tbodyHistorial.innerHTML = `<tr><td colspan="8" class="text-center py-4" style="color:var(--text-muted);">No hay tareas de picking finalizadas en el historial.</td></tr>`;
            } else {
                tbodyHistorial.innerHTML = historial.map(t => {
                    let badgeClass = (t.estado === 'COMPLETADA') ? 'badge-success' : 'badge-danger';
                    const pct = Math.min(100, Math.round((t.cantidadRecogida / t.cantidadRequerida) * 100));

                    return `
                        <tr>
                            <td style="font-family:var(--font-mono); color:var(--text-muted); font-weight:600;">${t.codigoTarea}</td>
                            <td><strong style="color:var(--text-bright); font-family:var(--font-mono);">${t.codigoPedido || 'PED-' + t.pedidoId}</strong></td>
                            <td><strong>${t.productoNombre}</strong></td>
                            <td><span class="badge badge-info">${t.codigoUbicacion || 'Estante General'}</span></td>
                            <td><strong>${t.cantidadRequerida}</strong> u.</td>
                            <td>
                                <div><strong>${t.cantidadRecogida}</strong> / ${t.cantidadRequerida} u.</div>
                                <div class="progress-bar-container" style="height:4px; margin-top:2px;">
                                    <div class="progress-bar-fill" style="width:${pct}%;"></div>
                                </div>
                            </td>
                            <td><span class="badge ${badgeClass}">${t.estado}</span></td>
                            <td>
                                <span class="badge ${t.estado === 'COMPLETADA' ? 'badge-success' : 'badge-danger'}">
                                    ${t.estado === 'COMPLETADA' ? 'Recolectado 100%' : 'Cancelado'}
                                </span>
                            </td>
                        </tr>
                    `;
                }).join('');
            }
        }
    }
};


window.PickingRenderer = PickingRenderer;
