/* ==========================================
   LogiTrack S.A. - Pedido UI Renderer
   ========================================== */

const PedidoRenderer = {
    renderTable(pedidos) {
        const list = Array.isArray(pedidos) ? pedidos : (pedidos && Array.isArray(pedidos.content) ? pedidos.content : []);

        const activos = list.filter(p => p.estado !== 'DESPACHADO' && p.estado !== 'ENTREGADO' && p.estado !== 'CANCELADO');
        const historial = list.filter(p => p.estado === 'DESPACHADO' || p.estado === 'ENTREGADO' || p.estado === 'CANCELADO');

        // Actualizar contadores
        const badgeActivos = document.getElementById('contador-pedidos-activos');
        if (badgeActivos) badgeActivos.innerText = `${activos.length} activos`;

        const badgeHistorial = document.getElementById('contador-pedidos-historial');
        if (badgeHistorial) badgeHistorial.innerText = `${historial.length} despachados`;

        const currentUser = typeof AuthService !== 'undefined' ? AuthService.getCurrentUser() : null;
        const canOperate = currentUser && (currentUser.rol === 'ADMIN' || currentUser.rol === 'SUPERVISOR' || currentUser.rol === 'EMPLEADO' || currentUser.rol === 'SUPER_ADMIN');
        const canCancel = currentUser && (currentUser.rol === 'ADMIN' || currentUser.rol === 'SUPERVISOR' || currentUser.rol === 'SUPER_ADMIN');

        // 1. Render Pedidos Activos en Proceso
        const tbodyActivos = document.getElementById('tabla-pedidos-activos-body');
        if (tbodyActivos) {
            if (activos.length === 0) {
                tbodyActivos.innerHTML = `<tr><td colspan="8" class="text-center py-4" style="color:var(--text-muted);">No hay pedidos en preparación o pendientes de despacho.</td></tr>`;
            } else {
                tbodyActivos.innerHTML = activos.map(p => {
                    let badgeClass = 'badge-warning';
                    if (p.estado === 'EN_PREPARACION') badgeClass = 'badge-info';
                    if (p.estado === 'EMPACADO') badgeClass = 'badge-purple';

                    const fecha = p.fechaPedido ? new Date(p.fechaPedido).toLocaleDateString() : '-';
                    const comp = p.fechaCompromiso || '-';
                    const total = p.totalPedido ? `$${p.totalPedido.toLocaleString('es-CO')}` : '$0';

                    return `
                        <tr>
                            <td style="font-family:var(--font-mono); font-weight:600; color:var(--accent);">${p.codigoPedido}</td>
                            <td><strong>${p.clienteNombre}</strong></td>
                            <td>${p.bodegaOrigenNombre}</td>
                            <td style="font-size:12px;">${fecha}</td>
                            <td style="font-size:12px;">${comp}</td>
                            <td style="font-weight:600; color:var(--text-bright);">${total}</td>
                            <td><span class="badge ${badgeClass}">${p.estado}</span></td>
                            <td>
                                <div class="table-actions">
                                    ${canOperate ? `<button class="btn btn-sm btn-primary" style="padding: 3px 8px; font-size: 11px;" title="Despachar pedido" onclick="PedidoController.despachar(${p.id})">Despachar</button>` : ''}
                                    ${canCancel ? `<button class="btn btn-sm btn-danger" style="padding: 3px 8px; font-size: 11px;" title="Cancelar pedido" onclick="PedidoController.cancelar(${p.id})">Cancelar</button>` : ''}
                                    ${(!canOperate && !canCancel) ? '<span style="color:var(--text-muted); font-size:11px;">Lectura</span>' : ''}
                                </div>
                            </td>
                        </tr>
                    `;
                }).join('');
            }
        }

        // 2. Render Historial de Pedidos Despachados y Finalizados
        const tbodyHistorial = document.getElementById('tabla-pedidos-historial-body');
        if (tbodyHistorial) {
            if (historial.length === 0) {
                tbodyHistorial.innerHTML = `<tr><td colspan="8" class="text-center py-4" style="color:var(--text-muted);">No hay pedidos despachados o finalizados en el historial.</td></tr>`;
            } else {
                tbodyHistorial.innerHTML = historial.map(p => {
                    let badgeClass = 'badge-success';
                    if (p.estado === 'CANCELADO') badgeClass = 'badge-danger';

                    const fecha = p.fechaPedido ? new Date(p.fechaPedido).toLocaleDateString() : '-';
                    const comp = p.fechaCompromiso || '-';
                    const total = p.totalPedido ? `$${p.totalPedido.toLocaleString('es-CO')}` : '$0';

                    return `
                        <tr>
                            <td style="font-family:var(--font-mono); font-weight:600; color:var(--text-muted);">${p.codigoPedido}</td>
                            <td><strong>${p.clienteNombre}</strong></td>
                            <td>${p.bodegaOrigenNombre}</td>
                            <td style="font-size:12px;">${fecha}</td>
                            <td style="font-size:12px;">${comp}</td>
                            <td style="font-weight:600; color:var(--text-bright);">${total}</td>
                            <td><span class="badge ${badgeClass}">${p.estado}</span></td>
                            <td>
                                <span class="badge ${p.estado === 'DESPACHADO' ? 'badge-info' : p.estado === 'ENTREGADO' ? 'badge-success' : 'badge-danger'}">
                                    ${p.estado === 'DESPACHADO' ? 'Enviado / En Tránsito' : p.estado === 'ENTREGADO' ? 'Entregado al Cliente' : 'Cancelado'}
                                </span>
                            </td>
                        </tr>
                    `;
                }).join('');
            }
        }
    }
};

window.PedidoRenderer = PedidoRenderer;
