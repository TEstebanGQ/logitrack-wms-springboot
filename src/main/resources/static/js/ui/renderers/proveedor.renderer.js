/* ==========================================
   LogiTrack S.A. - Proveedor UI Renderer
   ========================================== */

const ProveedorRenderer = {
    renderTable(proveedores, containerId = 'proveedores-list-container') {
        const container = document.getElementById(containerId);
        if (!container) return;

        const list = Array.isArray(proveedores) ? proveedores : (proveedores && Array.isArray(proveedores.content) ? proveedores.content : []);

        if (list.length === 0) {
            container.innerHTML = `<p style="padding: 1rem; color: var(--text-muted);">No hay proveedores registrados.</p>`;
            return;
        }

        const currentUser = AuthService.getCurrentUser();
        const canManage = currentUser && (currentUser.rol === 'ADMIN' || currentUser.rol === 'SUPERVISOR' || currentUser.rol === 'JEFE_COMPRAS' || currentUser.rol === 'GERENTE_LOGISTICA');
        const isAdmin = currentUser && currentUser.rol === 'ADMIN';

        const rows = list.map(p => `
            <tr>
                <td>#${p.id}</td>
                <td><strong>${p.nombre}</strong></td>
                <td><span style="font-family:var(--font-mono);">${p.ruc || 'N/A'}</span></td>
                <td>${p.telefono || 'N/A'}</td>
                <td>${p.email || 'N/A'}</td>
                <td><small style="color:var(--text-muted);">${p.direccion || 'N/A'}</small></td>
                <td><span class="badge ${p.activo ? 'badge-success' : 'badge-warning'}">${p.activo ? 'Activo' : 'Inactivo'}</span></td>
                <td>
                    <div style="display:flex; gap:6px; flex-wrap:wrap;">
                        <button class="btn btn-secondary btn-sm" style="padding: 2px 8px; font-size: 11px;" onclick="ProveedorModuleController.verMovimientos(${p.id}, '${p.nombre.replace(/'/g, "\\'")}')">📥 Entradas/Suministros</button>
                        ${canManage ? `
                            <button class="btn btn-secondary btn-sm" style="padding: 2px 8px; font-size: 11px;" onclick="ProveedorModuleController.edit(${p.id})">Editar</button>
                        ` : ''}
                        ${isAdmin ? `
                            <button class="btn btn-danger btn-sm" style="padding: 2px 8px; font-size: 11px;" onclick="ProveedorModuleController.delete(${p.id})">Eliminar</button>
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
                            <th>Razón Social / Proveedor</th>
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

window.ProveedorRenderer = ProveedorRenderer;
