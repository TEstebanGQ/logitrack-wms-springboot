/**
 * Renderizador de Órdenes de Compra
 */
export const ordenCompraRenderer = {
  renderTabla: (ordenes) => {
    const tbody = document.getElementById('tabla-ordenes-body');
    if (!tbody) return;

    if (!ordenes || ordenes.length === 0) {
      tbody.innerHTML = '<tr><td colspan="8" class="text-center py-4">No hay órdenes de compra registradas.</td></tr>';
      return;
    }

    tbody.innerHTML = ordenes.map(o => {
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
          <button class="btn-icon btn-aprobar-orden" data-id="${o.id}" title="Aprobar Orden">
            <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><polyline points="20 6 9 17 4 12"></polyline></svg>
          </button>
          <button class="btn-icon btn-cancelar-orden text-danger" data-id="${o.id}" title="Cancelar Orden">
            <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><line x1="18" y1="6" x2="6" y2="18"></line><line x1="6" y1="6" x2="18" y2="18"></line></svg>
          </button>
        `;
      } else if (o.estado === 'APROBADA') {
        acciones = `
          <button class="btn btn-sm btn-success btn-recibir-orden" data-id="${o.id}">
            📥 Recibir en Bodega
          </button>
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
