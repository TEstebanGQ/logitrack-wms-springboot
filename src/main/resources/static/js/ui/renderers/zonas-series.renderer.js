/* ==========================================
   LogiTrack S.A. - Zonas & Series UI Renderer
   ========================================== */

const ZonasSeriesRenderer = {
    renderSeries(series) {
        const tbody = document.getElementById('tabla-series-body');
        const badgeCount = document.getElementById('contador-series');
        if (badgeCount) badgeCount.innerText = `${series ? series.length : 0} series`;
        if (!tbody) return;

        if (!series || series.length === 0) {
            tbody.innerHTML = `<tr><td colspan="6" class="text-center py-4" style="color:var(--text-muted);">No hay números de serie registrados.</td></tr>`;
            return;
        }

        tbody.innerHTML = series.map(s => {
            let badgeClass = 'badge-info';
            if (s.estado === 'EN_STOCK') badgeClass = 'badge-success';
            if (s.estado === 'DESPACHADO') badgeClass = 'badge-warning';
            if (s.estado === 'DEFECTUOSO' || s.estado === 'DE_BAJA') badgeClass = 'badge-danger';

            const fecha = s.fechaIngreso ? new Date(s.fechaIngreso).toLocaleDateString() : '-';

            return `
                <tr>
                    <td style="font-family:var(--font-mono); font-weight:600; color:var(--accent);">${s.numeroSerie}</td>
                    <td><strong>${s.productoNombre}</strong></td>
                    <td>${s.bodegaNombre || '-'} <small style="color:var(--text-muted);">${s.codigoUbicacion ? '(' + s.codigoUbicacion + ')' : ''}</small></td>
                    <td style="font-size:12px;">${fecha}</td>
                    <td><span class="badge ${badgeClass}">${s.estado}</span></td>
                    <td><small style="color:var(--text-muted);">${s.observaciones || '-'}</small></td>
                </tr>
            `;
        }).join('');
    },

    renderZonas(zonas) {
        const tbody = document.getElementById('tabla-zonas-body');
        const badgeCount = document.getElementById('contador-zonas');
        if (badgeCount) badgeCount.innerText = `${zonas ? zonas.length : 0} zonas`;
        if (!tbody) return;

        if (!zonas || zonas.length === 0) {
            tbody.innerHTML = `<tr><td colspan="4" class="text-center py-4" style="color:var(--text-muted);">No hay zonas registradas.</td></tr>`;
            return;
        }

        tbody.innerHTML = zonas.map(z => `
            <tr>
                <td style="font-family:var(--font-mono);">${z.codigo}</td>
                <td><strong>${z.nombre}</strong></td>
                <td><span class="badge badge-info">${z.tipoZona}</span></td>
                <td>${z.bodegaNombre}</td>
            </tr>
        `).join('');
    },

    renderUnidades(unidades) {
        const tbody = document.getElementById('tabla-unidades-body');
        const badgeCount = document.getElementById('contador-unidades');
        if (badgeCount) badgeCount.innerText = `${unidades ? unidades.length : 0} unidades`;
        if (!tbody) return;

        if (!unidades || unidades.length === 0) {
            tbody.innerHTML = `<tr><td colspan="4" class="text-center py-4" style="color:var(--text-muted);">No hay unidades registradas.</td></tr>`;
            return;
        }

        tbody.innerHTML = unidades.map(u => `
            <tr>
                <td style="font-family:var(--font-mono); font-weight:600;">${u.codigo}</td>
                <td><strong>${u.nombre}</strong></td>
                <td>${u.abreviatura || '-'}</td>
                <td><span class="badge badge-success">x${u.factorConversion}</span></td>
            </tr>
        `).join('');
    }
};

window.ZonasSeriesRenderer = ZonasSeriesRenderer;
