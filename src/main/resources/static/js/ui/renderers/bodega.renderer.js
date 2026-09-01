/* ==========================================
   LogiTrack S.A. - Bodega UI Renderer
   ========================================== */

const BodegaRenderer = {
    renderTable(bodegas, containerId = 'bodegas-list-container') {
        const container = document.getElementById(containerId);
        if (!container) return;

        const list = Array.isArray(bodegas) ? bodegas : (bodegas && Array.isArray(bodegas.content) ? bodegas.content : []);

        if (list.length === 0) {
            container.innerHTML = `<p style="padding: 1rem; color: var(--text-muted);">No hay bodegas registradas.</p>`;
            return;
        }

        const currentUser = typeof AuthService !== 'undefined' ? AuthService.getCurrentUser() : null;
        const canManage = currentUser && (currentUser.rol === 'ADMIN' || currentUser.rol === 'SUPER_ADMIN');

        const rows = list.map(b => `
            <tr>
                <td>#${b.id}</td>
                <td><strong>${b.nombre}</strong></td>
                <td>${b.ubicacion}</td>
                <td>${b.capacidad ? b.capacidad.toLocaleString() : 0} unidades</td>
                <td>${b.encargado || '-'}</td>
                <td><span class="badge ${b.activo !== false ? 'badge-success' : 'badge-danger'}">${b.activo !== false ? 'Activo' : 'Inactivo'}</span></td>
                <td>
                    <button class="btn btn-secondary btn-sm" onclick="BodegaModuleController.verInventario(${b.id})">Inventario</button>
                    ${canManage ? `
                        <button class="btn btn-secondary btn-sm" onclick="BodegaModuleController.edit(${b.id})">Editar</button>
                        <button class="btn btn-danger btn-sm" onclick="BodegaModuleController.delete(${b.id})">Eliminar</button>
                    ` : ''}
                </td>
            </tr>
        `).join('');


        container.innerHTML = `
            <div class="table-container">
                <table class="table">
                    <thead>
                        <tr>
                            <th>ID</th>
                            <th>Nombre</th>
                            <th>Ubicación</th>
                            <th>Capacidad</th>
                            <th>Encargado</th>
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

window.BodegaRenderer = BodegaRenderer;
