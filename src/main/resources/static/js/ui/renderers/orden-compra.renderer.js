/* ==========================================
   LogiTrack S.A. - Renderizador UI de Órdenes de Compra
   ========================================== */

const OrdenCompraRenderer = {
    renderTabla(ordenes) {
        const tbody = document.getElementById('tabla-ordenes-body');
        if (!tbody) return;

        const list = Array.isArray(ordenes) ? ordenes : (ordenes && Array.isArray(ordenes.content) ? ordenes.content : []);

        if (list.length === 0) {
            tbody.innerHTML = '<tr><td colspan="8" class="text-center py-4" style="color:var(--text-muted);">No hay órdenes de compra registradas.</td></tr>';
            return;
        }

        tbody.innerHTML = list.map(o => {
            const fechaSol = o.fechaSolicitud ? new Date(o.fechaSolicitud).toLocaleDateString() : 'N/A';
            const fechaEnt = o.fechaEntregaEsperada ? o.fechaEntregaEsperada : 'No definida';
            const totalFmt = new Intl.NumberFormat('es-CO', { style: 'currency', currency: 'COP', maximumFractionDigits: 0 }).format(o.totalEstimado || 0);

            let estadoBadge = '<span class="badge badge-warning">PENDIENTE</span>';
            if (o.estado === 'APROBADA') estadoBadge = '<span class="badge badge-info">APROBADA</span>';
            if (o.estado === 'RECIBIDA') estadoBadge = '<span class="badge badge-success">RECIBIDA</span>';
            if (o.estado === 'CANCELADA') estadoBadge = '<span class="badge badge-danger">CANCELADA</span>';

            let acciones = '';
            if (o.estado === 'PENDIENTE') {
                acciones = `
                    <button class="btn btn-sm btn-info" onclick="OrdenCompraController.aprobar(${o.id})" title="Aprobar Orden">✓ APROBAR</button>
                    <button class="btn btn-sm btn-danger" onclick="OrdenCompraController.cancelar(${o.id})" title="Cancelar Orden">✕ CANCELAR</button>
                `;
            } else if (o.estado === 'APROBADA') {
                acciones = `
                    <button class="btn btn-sm btn-primary" onclick="OrdenCompraController.recibir(${o.id})">📥 RECIBIR</button>
                `;
            }

            return `
                <tr>
                    <td><strong>${o.codigoOrden}</strong></td>
                    <td>${fechaSol}</td>
                    <td>${o.proveedorNombre || 'N/A'}</td>
                    <td>${o.bodegaDestinoNombre || 'N/A'}</td>
                    <td><strong>${totalFmt}</strong></td>
                    <td>${estadoBadge}</td>
                    <td>${fechaEnt}</td>
                    <td><div class="table-actions">${acciones}</div></td>
                </tr>
            `;
        }).join('');
    }
};

window.OrdenCompraRenderer = OrdenCompraRenderer;
