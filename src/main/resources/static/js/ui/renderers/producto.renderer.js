/* ==========================================
   LogiTrack S.A. - Producto UI Renderer
   ========================================== */

const ProductoRenderer = {
    renderTable(productos, containerId = 'productos-list-container') {
        const container = document.getElementById(containerId);
        if (!container) return;

        const list = Array.isArray(productos) ? productos : (productos && Array.isArray(productos.content) ? productos.content : []);

        if (list.length === 0) {
            container.innerHTML = `<p style="padding: 1rem; color: var(--text-muted);">No hay productos registrados.</p>`;
            return;
        }

        const currentUser = typeof AuthService !== 'undefined' ? AuthService.getCurrentUser() : null;
        const canManage = currentUser && currentUser.rol === 'ADMIN';


        const getCatClass = (cat) => {
            if (!cat) return 'badge-info';
            const norm = cat.toLowerCase();
            if (norm.includes('mobil') || norm.includes('muebl')) return 'badge-cat-mobiliario';
            if (norm.includes('electr') || norm.includes('tech')) return 'badge-cat-electronica';
            if (norm.includes('perifer') || norm.includes('accesor')) return 'badge-cat-perifericos';
            if (norm.includes('papel')) return 'badge-cat-papeleria';
            return 'badge-info';
        };

        const rows = list.map(p => {
            const lowStock = p.stock < (p.stockMinimo || 10);
            const catName = p.categoriaNombre || p.categoria || 'General';
            const catBadgeClass = getCatClass(catName);

            return `
                <tr style="cursor:pointer;" onclick="App.openProductoDrawer(${p.id})" title="Haga clic para abrir ficha completa del producto en el panel lateral">
                    <td><span class="mono" style="font-weight:700;">#${p.id}</span></td>
                    <td><strong>${p.nombre}</strong></td>
                    <td><span class="badge ${catBadgeClass}">${catName}</span></td>
                    <td>
                        <span class="badge ${lowStock ? 'badge-danger' : 'badge-success'}">
                            ${p.stock} unidades ${lowStock ? 'Bajo Stock' : ''}
                        </span>
                    </td>
                    <td><strong style="color:var(--accent); font-family:var(--font-mono);">$${Number(p.precio).toLocaleString('es-CO')}</strong></td>
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
