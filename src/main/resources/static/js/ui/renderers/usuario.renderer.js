/* ==========================================
   LogiTrack S.A. - Usuario UI Renderer
   ========================================== */

const UsuarioRenderer = {
    renderTable(usuarios, containerId = 'usuarios-list-container') {
        const container = document.getElementById(containerId);
        if (!container) return;

        const list = Array.isArray(usuarios) ? usuarios : (usuarios && Array.isArray(usuarios.content) ? usuarios.content : []);

        if (list.length === 0) {
            container.innerHTML = `<p style="padding: 1rem; color: var(--text-muted);">No hay usuarios registrados.</p>`;
            return;
        }

        const currentUser = AuthService.getCurrentUser();
        const currentUserId = currentUser ? currentUser.id : null;
        const currentUserEmail = currentUser ? currentUser.email : null;

        const rows = list.map(u => {
            const isSelf = (currentUserId && u.id === currentUserId) || (currentUserEmail && u.email === currentUserEmail);
            const rolBadge = u.rol === 'ADMIN' ? 'badge-danger' : 'badge-info';
            const estadoBadge = u.activo ? 'badge-success' : 'badge-warning';

            return `
                <tr>
                    <td>#${u.id}</td>
                    <td><strong>${u.nombre} ${u.apellido}</strong></td>
                    <td>${u.email}</td>
                    <td><span class="badge ${rolBadge}">${u.rol}</span></td>
                    <td><span class="badge ${estadoBadge}">${u.activo ? 'Activo' : 'Inactivo'}</span></td>
                    <td>
                        <button class="btn btn-secondary btn-sm" onclick="UsuarioModuleController.edit(${u.id})">Editar</button>
                        ${!isSelf ? `
                            <button class="btn ${u.activo ? 'btn-secondary' : 'btn-primary'} btn-sm" 
                                    onclick="UsuarioModuleController.toggleStatus(${u.id}, ${!u.activo})">
                                ${u.activo ? 'Desactivar' : 'Activar'}
                            </button>
                            <button class="btn btn-danger btn-sm" onclick="UsuarioModuleController.delete(${u.id})">Eliminar</button>
                        ` : '<span style="font-size: 0.8rem; color: var(--text-muted);">(Tú)</span>'}
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
                            <th>Nombre Completo</th>
                            <th>Email</th>
                            <th>Rol</th>
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

window.UsuarioRenderer = UsuarioRenderer;
