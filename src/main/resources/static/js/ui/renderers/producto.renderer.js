/* ==========================================
   LogiTrack S.A. - Producto UI Renderer
   ========================================== */

const ProductoRenderer = {
    renderTable(productos, containerId = 'productos-list-container') {
        const container = document.getElementById(containerId);
        if (!container) return;

        if (!productos || productos.length === 0) {
            container.innerHTML = `<p style="padding: 1rem; color: var(--text-muted);">No hay productos registrados.</p>`;
            return;
        }

        const currentUser = typeof AuthService !== 'undefined' ? AuthService.getCurrentUser() : null;
        const canManage = currentUser && (currentUser.rol === 'ADMIN' || currentUser.rol === 'SUPERVISOR' || currentUser.rol === 'JEFE_COMPRAS');

        const rows = productos.map(p => {
            const lowStock = p.stock < (p.stockMinimo || 10);
            return `
                <tr style="cursor:pointer;" onclick="App.openProductoDrawer(${p.id})" title="Haga clic para abrir ficha completa del producto en el panel lateral">
                    <td>#${p.id}</td>
                    <td><strong>${p.nombre}</strong></td>
                    <td><span class="badge badge-info">${p.categoriaNombre || p.categoria || 'General'}</span></td>
                    <td>
                        <span class="badge ${lowStock ? 'badge-danger' : 'badge-success'}">
                            ${p.stock} unidades ${lowStock ? '⚠️ Bajo Stock' : ''}
                        </span>
                    </td>
                    <td>$${Number(p.precio).toLocaleString('es-CO')}</td>
                    <td>${p.descripcion || '-'}</td>
                    <td onclick="event.stopPropagation();">
                        ${canManage ? `
                            <button class="btn btn-secondary btn-sm" onclick="ProductoModuleController.edit(${p.id})">Editar</button>
                            <button class="btn btn-danger btn-sm" onclick="ProductoModuleController.delete(${p.id})">Eliminar</button>
                        ` : '<span style="color:var(--text-muted); font-size:11px;">Lectura</span>'}
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
                            <th>Nombre</th>
                            <th>Categoría</th>
                            <th>Stock</th>
                            <th>Precio</th>
                            <th>Descripción</th>
                            <th>Acciones</th>
                        </tr>
                    </thead>
                    <tbody>${rows}</tbody>
                </table>
            </div>
        `;
    }
};

window.ProductoRenderer = ProductoRenderer;
