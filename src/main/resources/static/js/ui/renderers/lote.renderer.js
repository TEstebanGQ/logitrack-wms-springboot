/* ==========================================
   LogiTrack S.A. - Renderizador UI de Lotes y FEFO
   ========================================== */

const LoteRenderer = {
    renderTabla(lotes) {
        const tbody = document.getElementById('tabla-lotes-body');
        if (!tbody) return;

        const list = Array.isArray(lotes) ? lotes : (lotes && Array.isArray(lotes.content) ? lotes.content : []);

        if (list.length === 0) {
            tbody.innerHTML = '<tr><td colspan="8" class="text-center py-4" style="color:var(--text-muted);">No hay lotes registrados.</td></tr>';
            return;
        }

        tbody.innerHTML = list.map(l => {
            const fechaFab = l.fechaFabricacion || 'No registrada';
            const fechaVenc = l.fechaVencimiento || 'No registrada';

            let estadoBadge = '<span class="badge badge-success">DISPONIBLE</span>';
            if (l.estado === 'CUARENTENA') estadoBadge = '<span class="badge badge-warning">CUARENTENA</span>';
            if (l.estado === 'VENCIDO' || l.vencido) estadoBadge = '<span class="badge badge-danger">VENCIDO</span>';
            if (l.estado === 'AGOTADO') estadoBadge = '<span class="badge badge-secondary">AGOTADO</span>';
            if (l.proximoAVencer && l.estado === 'DISPONIBLE') {
                estadoBadge += ' <span class="badge badge-warning">⚠️ PRÓXIMO A VENCER</span>';
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
                            <button class="btn btn-sm btn-secondary" onclick="LoteController.cambiarEstado(${l.id})" title="Cambiar Estado">ESTADO</button>
                        </div>
                    </td>
                </tr>
            `;
        }).join('');
    }
};

window.LoteRenderer = LoteRenderer;
