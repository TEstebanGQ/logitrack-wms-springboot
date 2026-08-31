/* ==========================================
   LogiTrack S.A. - Auditoria UI Renderer
   ========================================== */

const AuditoriaRenderer = {
    allAuditorias: [],

    renderTable(auditorias, containerId = 'auditorias-list-container') {
        const container = document.getElementById(containerId);
        if (!container) return;

        this.allAuditorias = Array.isArray(auditorias) ? auditorias : (auditorias && Array.isArray(auditorias.content) ? auditorias.content : []);
        window.auditoriaDataStore = this.allAuditorias;

        container.innerHTML = `
            <!-- Barra de Filtros Avanzados -->
            <div class="card" style="margin-bottom: 18px; padding: 14px 18px; background: rgba(255,255,255,0.02); border: 1px solid var(--border-color);">
                <div style="display: flex; gap: 12px; flex-wrap: wrap; align-items: center;">
                    <div style="flex: 1; min-width: 220px;">
                        <input type="text" id="aud-search-input" class="form-input" placeholder="Buscar por producto, bodega, usuario..." oninput="AuditoriaRenderer.filterTable()">
                    </div>
                    <div style="width: 170px;">
                        <select id="aud-filter-entidad" class="form-select" onchange="AuditoriaRenderer.filterTable()">
                            <option value="">Todas las Entidades</option>
                            <option value="Producto">Productos</option>
                            <option value="Bodega">Bodegas</option>
                            <option value="Movimiento">Movimientos</option>
                            <option value="Categoría">Categorías</option>
                        </select>
                    </div>
                    <div style="width: 170px;">
                        <select id="aud-filter-operacion" class="form-select" onchange="AuditoriaRenderer.filterTable()">
                            <option value="">Todas las Operaciones</option>
                            <option value="INSERT">INSERT (Creación)</option>
                            <option value="UPDATE">UPDATE (Edición)</option>
                            <option value="DELETE">DELETE (Eliminación)</option>
                        </select>
                    </div>
                </div>
            </div>

            <div id="aud-table-body-wrap">
                ${this.buildTableHtml(this.allAuditorias)}
            </div>
        `;
    },

    filterTable() {
        const query = (document.getElementById('aud-search-input')?.value || '').toLowerCase();
        const entidad = document.getElementById('aud-filter-entidad')?.value || '';
        const operacion = document.getElementById('aud-filter-operacion')?.value || '';

        const filtered = this.allAuditorias.filter(a => {
            const matchEntidad = !entidad || (a.entidad && a.entidad.toLowerCase() === entidad.toLowerCase());
            const matchOperacion = !operacion || a.tipoOperacion === operacion;

            const textSearch = `${a.id} ${a.entidad} ${a.recursoNombre || ''} ${a.entidadId || ''} ${a.tipoOperacion} ${a.usuarioEmail || ''} ${a.descripcion || ''} ${a.valoresAnteriores || ''} ${a.valoresNuevos || ''}`.toLowerCase();
            const matchQuery = !query || textSearch.includes(query);

            return matchEntidad && matchOperacion && matchQuery;
        });

        const wrap = document.getElementById('aud-table-body-wrap');
        if (wrap) wrap.innerHTML = this.buildTableHtml(filtered);
    },

    buildTableHtml(auditorias) {
        const list = Array.isArray(auditorias) ? auditorias : (auditorias && Array.isArray(auditorias.content) ? auditorias.content : []);

        if (list.length === 0) {
            return `<p style="padding: 1.5rem; text-align: center; color: var(--text-muted);">No se encontraron registros de auditoría que coincidan con los filtros.</p>`;
        }

        const rows = list.map(a => {
            let badgeClass = 'badge-info';
            if (a.tipoOperacion === 'INSERT') badgeClass = 'badge-success';
            if (a.tipoOperacion === 'DELETE') badgeClass = 'badge-danger';
            if (a.tipoOperacion === 'UPDATE') badgeClass = 'badge-warning';

            const fecha = a.fechaHora ? new Date(a.fechaHora).toLocaleString('es-CO') : '-';
            const usuarioStr = a.usuarioEmail || 'SISTEMA';
            const recursoNombreStr = a.recursoNombre || (a.entidad + ' #' + (a.entidadId || 'N/A'));

            return `
                <tr>
                    <td>#${a.id}</td>
                    <td><strong style="color:var(--text-bright);">${a.entidad}</strong></td>
                    <td>
                        <strong style="color:var(--accent-color); font-size:13px;">${recursoNombreStr}</strong>
                        <div style="font-size:11px; color:var(--text-dim);">ID #${a.entidadId || 'N/A'}</div>
                    </td>
                    <td><span class="badge ${badgeClass}">${a.tipoOperacion}</span></td>
                    <td>
                        <div style="font-size:12px; font-weight:500; color:var(--text-main);">${a.descripcion || 'Sin detalle registrado'}</div>
                    </td>
                    <td><span style="font-family:var(--font-mono); font-size:12px; color:var(--text-bright);">${usuarioStr}</span></td>
                    <td><small style="font-family:var(--font-mono); color:var(--text-dim);">${fecha}</small></td>
                    <td>
                        <button class="btn btn-secondary btn-sm" style="padding: 4px 10px; font-size: 11px;" onclick="AuditoriaModuleController.inspect(${a.id})"> Inspeccionar Cambios</button>
                    </td>
                </tr>
            `;
        }).join('');

        return `
            <div class="table-container">
                <table class="table">
                    <thead>
                        <tr>
                            <th>ID Audit</th>
                            <th>Entidad Afectada</th>
                            <th>Nombre del Recurso / Producto</th>
                            <th>Operación</th>
                            <th>Detalle Completo del Cambio</th>
                            <th>Usuario Responsable</th>
                            <th>Fecha y Hora</th>
                            <th>Acciones</th>
                        </tr>
                    </thead>
                    <tbody>${rows}</tbody>
                </table>
            </div>
        `;
    }
};

window.AuditoriaRenderer = AuditoriaRenderer;
