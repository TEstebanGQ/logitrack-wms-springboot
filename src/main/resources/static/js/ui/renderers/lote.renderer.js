/**
 * Renderizador de Lotes y Vencimientos
 */
export const loteRenderer = {
  renderTabla: (lotes) => {
    const tbody = document.getElementById('tabla-lotes-body');
    if (!tbody) return;

    if (!lotes || lotes.length === 0) {
      tbody.innerHTML = '<tr><td colspan="8" class="text-center py-4">No hay lotes registrados.</td></tr>';
      return;
    }

    tbody.innerHTML = lotes.map(l => {
      const fechaFab = l.fechaFabricacion || 'No registrada';
      const fechaVenc = l.fechaVencimiento || 'No registrada';

      let estadoBadge = '<span class="badge badge-success">DISPONIBLE</span>';
      if (l.estado === 'CUARENTENA') estadoBadge = '<span class="badge badge-warning">CUARENTENA</span>';
      if (l.estado === 'VENCIDO' || l.vencido) estadoBadge = '<span class="badge badge-danger">VENCIDO</span>';
      if (l.estado === 'AGOTADO') estadoBadge = '<span class="badge badge-secondary">AGOTADO</span>';
      if (l.proximoAVencer && l.estado === 'DISPONIBLE') {
        estadoBadge += ' <span class="badge badge-warning">⚠️ Próximo a vencer</span>';
      }

      return `
        <tr>
          <td><strong>${l.codigoLote}</strong></td>
          <td>${l.productoNombre || 'N/A'}</td>
          <td>${l.bodegaNombre || 'N/A'}</td>
          <td><strong>${l.stockActual}</strong> / ${l.stockInicial} u.</td>
          <td>${fechaFab}</td>
          <td>${fechaVenc}</td>
          <td>${estadoBadge}</td>
          <td>
            <div class="table-actions">
              <button class="btn-icon btn-cambiar-estado-lote" data-id="${l.id}" title="Cambiar Estado">
                <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M12 20h9"></path><path d="M16.5 3.5a2.121 2.121 0 0 1 3 3L7 19l-4 1 1-4L16.5 3.5z"></path></svg>
              </button>
            </div>
          </td>
        </tr>
      `;
    }).join('');
  }
};
