/* ==========================================
   LogiTrack S.A. - Movimiento UI Renderer
   ========================================== */

const MovimientoRenderer = {
    renderTable(movimientos, containerId = 'movimientos-list-container') {
        const container = document.getElementById(containerId);
        if (!container) return;

        const list = Array.isArray(movimientos) ? movimientos : (movimientos && Array.isArray(movimientos.content) ? movimientos.content : []);

        if (list.length === 0) {
            container.innerHTML = `<p style="padding: 1rem; color: var(--text-muted);">No hay movimientos registrados para el filtro seleccionado.</p>`;
            return;
        }

        const rows = list.map(m => {
            let badgeClass = 'badge-info';
            if (m.tipoMovimiento === 'ENTRADA') badgeClass = 'badge-success';
            if (m.tipoMovimiento === 'SALIDA') badgeClass = 'badge-danger';
            if (m.tipoMovimiento === 'TRANSFERENCIA') badgeClass = 'badge-warning';

            const fecha = m.fecha ? new Date(m.fecha).toLocaleString() : '-';

            const productosStr = (m.detalles && m.detalles.length > 0)
                ? m.detalles.map(d => `<strong>${d.productoNombre}</strong>`).join('<br>')
                : (m.productoNombre ? `<strong>${m.productoNombre}</strong>` : '-');

            const cantidadesStr = (m.detalles && m.detalles.length > 0)
                ? m.detalles.map(d => `<span class="badge badge-secondary" style="font-weight:600;">${d.cantidad} un.</span>`).join('<br>')
                : (m.cantidad ? `<span class="badge badge-secondary" style="font-weight:600;">${m.cantidad} un.</span>` : '-');

            const socioStr = m.proveedorNombre ? `<span style="color:var(--success);">${m.proveedorNombre}</span>` : (m.clienteNombre ? `<span style="color:var(--danger);">${m.clienteNombre}</span>` : '-');

            return `
                <tr style="cursor:pointer;" onclick="App.openMovimientoDrawer(${m.id})" title="Haga clic para abrir ficha completa en el panel lateral">
                    <td>#${m.id}</td>
                    <td><span class="badge ${badgeClass}">${m.tipoMovimiento}</span></td>
                    <td>${productosStr}</td>
                    <td>${cantidadesStr}</td>
                    <td>${fecha}</td>
                    <td>${m.usuarioNombre || '-'}</td>
                    <td>${m.bodegaOrigen || '-'}</td>
                    <td>${m.bodegaDestino || '-'}</td>
                    <td>${socioStr}</td>
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
                            <th>Producto</th>
                            <th>Cantidad</th>
                            <th>Fecha</th>
                            <th>Usuario</th>
                            <th>Origen</th>
                            <th>Destino</th>
                            <th>Tercero / Socio</th>
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
