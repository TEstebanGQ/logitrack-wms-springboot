/* ==========================================
   LogiTrack S.A. - Pedido UI Renderer
   ========================================== */

const PedidoRenderer = {
    renderTable(pedidos) {
        const tbody = document.getElementById('tabla-pedidos-body');
        const list = Array.isArray(pedidos) ? pedidos : (pedidos && Array.isArray(pedidos.content) ? pedidos.content : []);
        const badgeCount = document.getElementById('contador-pedidos');
        if (badgeCount) badgeCount.innerText = `${list.length} pedidos`;
        if (!tbody) return;

        if (list.length === 0) {
            tbody.innerHTML = `<tr><td colspan="8" class="text-center py-4" style="color:var(--text-muted);">No se encontraron pedidos de clientes.</td></tr>`;
            return;
        }

        tbody.innerHTML = list.map(p => {
            let badgeClass = 'badge-info';
            if (p.estado === 'PENDIENTE') badgeClass = 'badge-warning';
            if (p.estado === 'EN_PREPARACION') badgeClass = 'badge-info';
            if (p.estado === 'EMPACADO') badgeClass = 'badge-purple';
            if (p.estado === 'DESPACHADO') badgeClass = 'badge-success';
            if (p.estado === 'ENTREGADO') badgeClass = 'badge-success';
            if (p.estado === 'CANCELADO') badgeClass = 'badge-danger';

            const fecha = p.fechaPedido ? new Date(p.fechaPedido).toLocaleDateString() : '-';
            const comp = p.fechaCompromiso || '-';
            const total = p.totalPedido ? `$${p.totalPedido.toLocaleString('es-CO')}` : '$0';

            const canDespachar = p.estado !== 'DESPACHADO' && p.estado !== 'ENTREGADO' && p.estado !== 'CANCELADO';
            const canCancelar = p.estado !== 'DESPACHADO' && p.estado !== 'ENTREGADO' && p.estado !== 'CANCELADO';

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
                            ${canDespachar ? `<button class="btn btn-sm btn-primary" style="padding: 3px 8px; font-size: 11px;" title="Despachar pedido" onclick="PedidoController.despachar(${p.id})">Despachar</button>` : ''}
                            ${canCancelar ? `<button class="btn btn-sm btn-danger" style="padding: 3px 8px; font-size: 11px;" title="Cancelar pedido" onclick="PedidoController.cancelar(${p.id})">Cancelar</button>` : ''}
                        </div>
                    </td>
                </tr>

            `;
        }).join('');
    }
};

window.PedidoRenderer = PedidoRenderer;
