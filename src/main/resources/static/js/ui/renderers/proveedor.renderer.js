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
        const canManage = currentUser && (currentUser.rol === 'ADMIN' || currentUser.rol === 'SUPERVISOR' || currentUser.rol === 'GERENTE_LOGISTICA');
        const isAdmin = currentUser && currentUser.rol === 'ADMIN';


        const rows = list.map(p => `
            <tr>
                <td style="font-family:var(--font-mono); font-weight:600; color:var(--text-muted); text-align:center;">#${p.id}</td>
                <td><strong style="color:var(--text-bright); font-size:14px;">${p.nombre}</strong></td>
                <td><span style="font-family:var(--font-mono); font-weight:600;">${p.ruc || 'N/A'}</span></td>
                <td>${p.telefono || 'N/A'}</td>
                <td><span style="color:var(--info);">${p.email || 'N/A'}</span></td>
                <td><span style="color:var(--text-muted); font-size:12.5px;">${p.direccion || 'N/A'}</span></td>
                <td style="text-align:center;"><span class="badge ${p.activo ? 'badge-success' : 'badge-warning'}">${p.activo ? 'Activo' : 'Inactivo'}</span></td>
                <td style="text-align:center;">
                    <div style="display:flex; gap:6px; align-items:center; justify-content:center; flex-wrap:nowrap;">
                        <button class="btn btn-secondary btn-sm" style="padding:4px 10px; font-size:11px; white-space:nowrap;" onclick="ProveedorModuleController.verMovimientos(${p.id}, '${p.nombre.replace(/'/g, "\\'")}')"> Entradas / Suministros</button>
                        ${canManage ? `
                            <button class="btn btn-secondary btn-sm" style="padding:4px 10px; font-size:11px; white-space:nowrap;" onclick="ProveedorModuleController.edit(${p.id})">Editar</button>
                        ` : ''}
                        ${isAdmin ? `
                            <button class="btn btn-danger btn-sm" style="padding:4px 10px; font-size:11px; white-space:nowrap;" onclick="ProveedorModuleController.delete(${p.id})">Eliminar</button>
                        ` : ''}
                    </div>
                </td>
            </tr>
        `).join('');

        container.innerHTML = `
            <div class="table-container" style="width:100%; overflow-x:auto;">
                <table class="table" style="width:100%; border-collapse:collapse;">
                    <thead>
                        <tr>
                            <th style="width:60px; text-align:center;">ID</th>
                            <th>Razón Social / Proveedor</th>
                            <th style="width:140px;">RUC / NIT</th>
                            <th style="width:140px;">Teléfono</th>
                            <th style="width:190px;">Email</th>
                            <th>Dirección</th>
                            <th style="width:110px; text-align:center;">Estado</th>
                            <th style="width:250px; text-align:center;">Acciones</th>
                        </tr>
                    </thead>
                    <tbody>${rows}</tbody>
                </table>
            </div>
        `;
    }
};

window.ProveedorRenderer = ProveedorRenderer;
