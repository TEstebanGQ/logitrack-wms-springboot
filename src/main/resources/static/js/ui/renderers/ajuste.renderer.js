/* ==========================================
   LogiTrack S.A. - Renderizador UI de Ajustes de Inventario
   ========================================== */

const AjusteRenderer = {
    renderTabla(ajustes) {
        const tbody = document.getElementById('tabla-ajustes-body');
        if (!tbody) return;

        const list = Array.isArray(ajustes) ? ajustes : (ajustes && Array.isArray(ajustes.content) ? ajustes.content : []);

        if (list.length === 0) {
            tbody.innerHTML = '<tr><td colspan="10" class="text-center py-4" style="color:var(--text-muted);">No hay registros de ajustes o mermas.</td></tr>';
            return;
        }

        tbody.innerHTML = list.map(a => {
            const fechaFormateada = a.fecha ? new Date(a.fecha).toLocaleString() : 'N/A';
            const difBadge = a.diferencia < 0
                ? `<span class="badge badge-danger">${a.diferencia} u.</span>`
                : `<span class="badge badge-success">+${a.diferencia} u.</span>`;

            let tipoBadge = `<span class="badge badge-warning">${a.tipoAjuste}</span>`;
            if (a.tipoAjuste === 'MERMA' || a.tipoAjuste === 'DANO') {
                tipoBadge = `<span class="badge badge-danger">${a.tipoAjuste}</span>`;
            } else if (a.tipoAjuste === 'CONTEO_FISICO') {
                tipoBadge = `<span class="badge badge-info">${a.tipoAjuste}</span>`;
            }

            return `
                <tr>
                    <td><strong>#${a.id}</strong></td>
                    <td>${fechaFormateada}</td>
                    <td>${a.bodegaNombre || 'N/A'}</td>
                    <td>${a.productoNombre || 'N/A'}</td>
                    <td>${tipoBadge}</td>
                    <td>${a.cantidadAnterior}</td>
                    <td><strong>${a.cantidadNueva}</strong></td>
                    <td>${difBadge}</td>
                    <td>${a.justificacion || 'Sin justificación'}</td>
                    <td>${a.usuarioNombre || 'Sistema'}</td>
                </tr>
            `;
        }).join('');
    }
};

window.AjusteRenderer = AjusteRenderer;
