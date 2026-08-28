/* ==========================================
   LogiTrack S.A. - Cliente UI Renderer
   ========================================== */

const ClienteRenderer = {
    renderTable(clientes, containerId = 'clientes-list-container') {
        const container = document.getElementById(containerId);
        if (!container) return;

        if (!clientes || clientes.length === 0) {
            container.innerHTML = `<p style="padding: 1rem; color: var(--text-muted);">No hay clientes registrados.</p>`;
            return;
        }

        const currentUser = AuthService.getCurrentUser();
        const canManage = currentUser && (currentUser.rol === 'ADMIN' || currentUser.rol === 'SUPERVISOR' || currentUser.rol === 'GERENTE_LOGISTICA');
        const isAdmin = currentUser && currentUser.rol === 'ADMIN';

        const rows = clientes.map(c => `
            <tr>
                <td>#${c.id}</td>
                <td><strong>${c.nombre}</strong></td>
                <td><span style="font-family:var(--font-mono);">${c.ruc || 'N/A'}</span></td>
                <td>${c.telefono || 'N/A'}</td>
                <td>${c.email || 'N/A'}</td>
                <td><small style="color:var(--text-muted);">${c.direccion || 'N/A'}</small></td>
                <td><span class="badge ${c.activo ? 'badge-success' : 'badge-warning'}">${c.activo ? 'Activo' : 'Inactivo'}</span></td>
                <td>
                    <div style="display:flex; gap:6px; flex-wrap:wrap;">
                        <button class="btn btn-secondary btn-sm" style="padding: 2px 8px; font-size: 11px;" onclick="ClienteModuleController.verMovimientos(${c.id}, '${c.nombre.replace(/'/g, "\\'")}')">📦 Salidas/Despachos</button>
                        ${canManage ? `
                            <button class="btn btn-secondary btn-sm" style="padding: 2px 8px; font-size: 11px;" onclick="ClienteModuleController.edit(${c.id})">Editar</button>
                        ` : ''}
                        ${isAdmin ? `
                            <button class="btn btn-danger btn-sm" style="padding: 2px 8px; font-size: 11px;" onclick="ClienteModuleController.delete(${c.id})">Eliminar</button>
                        ` : ''}
                    </div>
                </td>
            </tr>
        `).join('');

        container.innerHTML = `
            <div class="table-container">
                <table class="table">
                    <thead>
                        <tr>
                            <th>ID</th>
                            <th>Razón Social / Nombre</th>
                            <th>RUC / NIT</th>
                            <th>Teléfono</th>
                            <th>Email</th>
                            <th>Dirección</th>
                            <th>Estado</th>
                            <th>Acciones</th>
                        </tr>
                    </thead>
                    <tbody>${rows}</tbody>
                </table>
            </div>
        `;
    }
};

window.ClienteRenderer = ClienteRenderer;
