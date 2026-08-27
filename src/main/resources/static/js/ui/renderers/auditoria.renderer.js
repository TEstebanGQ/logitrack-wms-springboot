/* ==========================================
   LogiTrack S.A. - Auditoria UI Renderer
   ========================================== */

const AuditoriaRenderer = {
    renderTable(auditorias, containerId = 'auditorias-list-container') {
        const container = document.getElementById(containerId);
        if (!container) return;

        if (!auditorias || auditorias.length === 0) {
            container.innerHTML = `<p style="padding: 1rem; color: var(--text-muted);">No hay registros de auditoría para el filtro seleccionado.</p>`;
            return;
        }

        window.auditoriaDataStore = auditorias;

        const rows = auditorias.map(a => {
            let badgeClass = 'badge-info';
            if (a.tipoOperacion === 'INSERT') badgeClass = 'badge-success';
            if (a.tipoOperacion === 'DELETE') badgeClass = 'badge-danger';
            if (a.tipoOperacion === 'UPDATE') badgeClass = 'badge-warning';

            const fecha = a.fechaHora ? new Date(a.fechaHora).toLocaleString() : '-';

            return `
                <tr>
                    <td>#${a.id}</td>
                    <td><strong>${a.entidad}</strong> (#${a.entidadId || '-'})</td>
                    <td><span class="badge ${badgeClass}">${a.tipoOperacion}</span></td>
                    <td>${fecha}</td>
                    <td>${a.usuarioId ? 'Usuario #' + a.usuarioId : 'Sistema'}</td>
                    <td><small style="color: var(--text-muted);">${a.descripcion || '-'}</small></td>
                    <td>
                        <button class="btn btn-secondary btn-sm" onclick="AuditoriaModuleController.inspect(${a.id})">👁️ Ver Detalle</button>
                    </td>
                </tr>
            `;
        }).join('');

        container.innerHTML = `
            <div class="table-container">
                <table class="table">
                    <thead>
                        <tr>
                            <th>ID</th>
                            <th>Entidad Afectada</th>
                            <th>Operación</th>
                            <th>Fecha/Hora</th>
                            <th>Usuario</th>
                            <th>Descripción</th>
                            <th>Acción</th>
                        </tr>
                    </thead>
                    <tbody>${rows}</tbody>
                </table>
            </div>
        `;
    }
};

window.AuditoriaRenderer = AuditoriaRenderer;
