/* ==========================================
   LogiTrack S.A. - Movimiento UI Renderer
   ========================================== */

const MovimientoRenderer = {
    renderTable(movimientos, containerId = 'movimientos-list-container') {
        const container = document.getElementById(containerId);
        if (!container) return;

        if (!movimientos || movimientos.length === 0) {
            container.innerHTML = `<p style="padding: 1rem; color: var(--text-muted);">No hay movimientos registrados para el filtro seleccionado.</p>`;
            return;
        }

        const rows = movimientos.map(m => {
            let badgeClass = 'badge-info';
            if (m.tipoMovimiento === 'ENTRADA') badgeClass = 'badge-success';
            if (m.tipoMovimiento === 'SALIDA') badgeClass = 'badge-danger';
            if (m.tipoMovimiento === 'TRANSFERENCIA') badgeClass = 'badge-warning';

            const fecha = m.fecha ? new Date(m.fecha).toLocaleString() : '-';

            return `
                <tr>
                    <td>#${m.id}</td>
                    <td><span class="badge ${badgeClass}">${m.tipoMovimiento}</span></td>
                    <td>${fecha}</td>
                    <td>${m.usuarioNombre || 'Usuario #' + (m.usuarioId || '-')}</td>
                    <td>${m.bodegaOrigenNombre || '-'}</td>
                    <td>${m.bodegaDestinoNombre || '-'}</td>
                    <td>${m.observaciones || '-'}</td>
                </tr>
            `;
        }).join('');

        container.innerHTML = `
            <div class="table-container">
                <table class="table">
                    <thead>
                        <tr>
                            <th>ID</th>
                            <th>Tipo</th>
                            <th>Fecha</th>
                            <th>Usuario</th>
                            <th>Origen</th>
                            <th>Destino</th>
                            <th>Observaciones</th>
                        </tr>
                    </thead>
                    <tbody>${rows}</tbody>
                </table>
            </div>
        `;
    }
};

window.MovimientoRenderer = MovimientoRenderer;
