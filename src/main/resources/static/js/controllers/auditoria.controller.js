/* ==========================================
   LogiTrack S.A. - Auditoria Controller Module
   ========================================== */

const AuditoriaModuleController = {
    async load(filtroOperacion = null) {
        try {
            let auditorias;
            if (filtroOperacion) {
                auditorias = await AuditoriaService.getByOperacion(filtroOperacion);
            } else {
                auditorias = await AuditoriaService.getAll();
            }
            AuditoriaRenderer.renderTable(auditorias);
        } catch (err) {
            console.error('Error cargando auditorías:', err);
            Toast.error('Error al cargar la auditoría');
        }
    },

    async loadByEntidad(entidad) {
        try {
            if (!entidad) {
                return this.load();
            }
            const auditorias = await AuditoriaService.getByEntidad(entidad);
            AuditoriaRenderer.renderTable(auditorias);
        } catch (err) {
            console.error('Error filtrando auditorías por entidad:', err);
            Toast.error('Error al filtrar auditorías por entidad');
        }
    },

    inspect(id) {
        const store = window.auditoriaDataStore || [];
        const item = store.find(a => a.id === id);
        if (!item) return;

        const titleEl = document.getElementById('aud-modal-title');
        const metaEl = document.getElementById('aud-metadata-container');
        const diffTableEl = document.getElementById('aud-diff-table-wrap');
        const prevEl = document.getElementById('aud-json-anteriores');
        const newEl = document.getElementById('aud-json-nuevos');

        if (titleEl) titleEl.innerText = `Ficha Completa de Auditoría #${item.id}`;

        let oldObj = null;
        let newObj = null;

        try { if (item.valoresAnteriores) oldObj = JSON.parse(item.valoresAnteriores); } catch (e) {}
        try { if (item.valoresNuevos) newObj = JSON.parse(item.valoresNuevos); } catch (e) {}

        if (metaEl) {
            let badgeClass = 'badge-info';
            if (item.tipoOperacion === 'INSERT') badgeClass = 'badge-success';
            if (item.tipoOperacion === 'DELETE') badgeClass = 'badge-danger';
            if (item.tipoOperacion === 'UPDATE') badgeClass = 'badge-warning';

            const fechaStr = item.fechaHora ? new Date(item.fechaHora).toLocaleString('es-CO') : '-';

            metaEl.innerHTML = `
                <div style="display: grid; grid-template-columns: 1fr 1fr; gap: 10px; font-size: 13px;">
                    <div><strong>Entidad Afectada:</strong> <span style="color:var(--accent-color); font-weight:600;">${item.entidad}</span> (ID #${item.entidadId || 'N/A'})</div>
                    <div><strong>Tipo Operación:</strong> <span class="badge ${badgeClass}">${item.tipoOperacion}</span></div>
                    <div><strong>Usuario Responsable:</strong> <span style="font-family:var(--font-mono);">${item.usuarioEmail || 'SISTEMA'}</span></div>
                    <div><strong>Fecha y Hora:</strong> <span style="font-family:var(--font-mono);">${fechaStr}</span></div>
                    <div style="grid-column: 1 / -1; margin-top: 4px; padding-top: 6px; border-top: 1px solid rgba(255,255,255,0.06); color: var(--text-bright);">
                        <strong>Descripción General:</strong> ${item.descripcion || 'Sin descripción'}
                    </div>
                </div>
            `;
        }

        // Generar tabla comparativa campo a campo (Visual Diff)
        if (diffTableEl) {
            diffTableEl.innerHTML = this.buildFieldDiffTable(item.tipoOperacion, oldObj, newObj);
        }

        // Cargar vista JSON para desarrolladores
        if (prevEl) prevEl.innerText = oldObj ? JSON.stringify(oldObj, null, 2) : '(Sin valores anteriores)';
        if (newEl) newEl.innerText = newObj ? JSON.stringify(newObj, null, 2) : '(Sin valores nuevos)';

        App.openModal('modal-auditoria');
    },

    buildFieldDiffTable(tipoOperacion, oldObj, newObj) {
        if (tipoOperacion === 'UPDATE' && oldObj && newObj) {
            const allKeys = Array.from(new Set([...Object.keys(oldObj), ...Object.keys(newObj)]));
            const changedKeys = allKeys.filter(k => JSON.stringify(oldObj[k]) !== JSON.stringify(newObj[k]));

            if (changedKeys.length === 0) {
                return `<p style="padding: 10px; color: var(--text-muted); font-size: 12px;">No se registraron diferencias entre los atributos.</p>`;
            }

            const rows = changedKeys.map(key => {
                const valOld = oldObj[key] !== undefined ? JSON.stringify(oldObj[key]) : '<em style="color:var(--text-dim)">Nulo</em>';
                const valNew = newObj[key] !== undefined ? JSON.stringify(newObj[key]) : '<em style="color:var(--text-dim)">Nulo</em>';

                return `
                    <tr>
                        <td><strong style="color:var(--accent-color); font-family:var(--font-mono);">${key}</strong></td>
                        <td style="color: #ef4444; background: rgba(239, 68, 68, 0.08); font-family:var(--font-mono); font-size:12px;">${valOld}</td>
                        <td style="color: #10b981; background: rgba(16, 185, 129, 0.08); font-family:var(--font-mono); font-size:12px;">${valNew}</td>
                    </tr>
                `;
            }).join('');

            return `
                <div style="margin-top: 12px;">
                    <h4 style="font-size: 12px; text-transform: uppercase; letter-spacing: 1px; color: var(--text-muted); margin-bottom: 8px;">
                        Atributos Modificados (Comparación Campo por Campo):
                    </h4>
                    <table class="table" style="font-size: 12px;">
                        <thead>
                            <tr>
                                <th>Campo Modificado</th>
                                <th>Valor Anterior</th>
                                <th>Valor Nuevo</th>
                            </tr>
                        </thead>
                        <tbody>${rows}</tbody>
                    </table>
                </div>
            `;
        } else if (tipoOperacion === 'INSERT' && newObj) {
            const rows = Object.entries(newObj).map(([key, val]) => `
                <tr>
                    <td><strong style="color:var(--accent-color); font-family:var(--font-mono);">${key}</strong></td>
                    <td style="color: #10b981; background: rgba(16, 185, 129, 0.08); font-family:var(--font-mono); font-size:12px;">${JSON.stringify(val)}</td>
                </tr>
            `).join('');

            return `
                <div style="margin-top: 12px;">
                    <h4 style="font-size: 12px; text-transform: uppercase; letter-spacing: 1px; color: #10b981; margin-bottom: 8px;">
                        Atributos del Recurso Creado:
                    </h4>
                    <table class="table" style="font-size: 12px;">
                        <thead>
                            <tr>
                                <th>Atributo</th>
                                <th>Valor Registrado</th>
                            </tr>
                        </thead>
                        <tbody>${rows}</tbody>
                    </table>
                </div>
            `;
        } else if (tipoOperacion === 'DELETE' && oldObj) {
            const rows = Object.entries(oldObj).map(([key, val]) => `
                <tr>
                    <td><strong style="color:var(--accent-color); font-family:var(--font-mono);">${key}</strong></td>
                    <td style="color: #ef4444; background: rgba(239, 68, 68, 0.08); font-family:var(--font-mono); font-size:12px;">${JSON.stringify(val)}</td>
                </tr>
            `).join('');

            return `
                <div style="margin-top: 12px;">
                    <h4 style="font-size: 12px; text-transform: uppercase; letter-spacing: 1px; color: #ef4444; margin-bottom: 8px;">
                        Atributos del Recurso Eliminado:
                    </h4>
                    <table class="table" style="font-size: 12px;">
                        <thead>
                            <tr>
                                <th>Atributo</th>
                                <th>Último Valor Registrado</th>
                            </tr>
                        </thead>
                        <tbody>${rows}</tbody>
                    </table>
                </div>
            `;
        }

        return '';
    }
};

window.AuditoriaModuleController = AuditoriaModuleController;
