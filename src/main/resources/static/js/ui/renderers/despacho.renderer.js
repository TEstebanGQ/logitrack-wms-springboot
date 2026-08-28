/* ==========================================
   LogiTrack S.A. - Despacho UI Renderer
   ========================================== */

const DespachoRenderer = {
    renderGuias(guias) {
        const tbody = document.getElementById('tabla-guias-body');
        const badgeCount = document.getElementById('contador-guias');
        if (badgeCount) badgeCount.innerText = `${guias ? guias.length : 0} guías`;
        if (!tbody) return;

        if (!guias || guias.length === 0) {
            tbody.innerHTML = `<tr><td colspan="8" class="text-center py-4" style="color:var(--text-muted);">No se encontraron guías de despacho.</td></tr>`;
            return;
        }

        tbody.innerHTML = guias.map(g => {
            let badgeClass = 'badge-info';
            if (g.estadoEnvio === 'EN_TRANSITO') badgeClass = 'badge-warning';
            if (g.estadoEnvio === 'ENTREGADO') badgeClass = 'badge-success';
            if (g.estadoEnvio === 'DEVUELTO' || g.estadoEnvio === 'CON_NOVEDAD') badgeClass = 'badge-danger';

            const fecha = g.fechaDespacho ? new Date(g.fechaDespacho).toLocaleDateString() : '-';

            return `
                <tr>
                    <td style="font-family:var(--font-mono); font-weight:600; color:var(--accent);">${g.numeroGuia}</td>
                    <td><strong>${g.codigoPedido || 'PED-' + g.pedidoId}</strong></td>
                    <td>${g.clienteNombre || '-'}</td>
                    <td>${g.transportadoraNombre}</td>
                    <td><small>${g.conductorNombre || '-'}<br><span style="color:var(--text-muted);">${g.placaVehiculo || ''}</span></small></td>
                    <td style="font-size:12px;">${fecha}</td>
                    <td><span class="badge ${badgeClass}">${g.estadoEnvio}</span></td>
                    <td>
                        ${g.estadoEnvio !== 'ENTREGADO' ? `<button class="btn btn-sm btn-success" onclick="DespachoController.marcarEntregado(${g.id})">✓ Entregado</button>` : '<span style="color:var(--success); font-size:12px;">✓ Completado</span>'}
                    </td>
                </tr>
            `;
        }).join('');
    },

    renderTransportadoras(transportadoras) {
        const tbody = document.getElementById('tabla-transportadoras-body');
        const badgeCount = document.getElementById('contador-transportadoras');
        if (badgeCount) badgeCount.innerText = `${transportadoras ? transportadoras.length : 0} empresas`;
        if (!tbody) return;

        if (!transportadoras || transportadoras.length === 0) {
            tbody.innerHTML = `<tr><td colspan="7" class="text-center py-4" style="color:var(--text-muted);">No hay transportadoras registradas.</td></tr>`;
            return;
        }

        tbody.innerHTML = transportadoras.map(t => `
            <tr>
                <td style="font-family:var(--font-mono);">#${t.id}</td>
                <td><strong>${t.nombre}</strong></td>
                <td>${t.rucNit}</td>
                <td><span class="badge badge-info">${t.tipoServicio}</span></td>
                <td><small>${t.contacto || '-'}<br>${t.telefono || ''}</small></td>
                <td><small style="color:var(--text-muted);">${t.email || '-'}</small></td>
                <td><span class="badge ${t.activo ? 'badge-success' : 'badge-danger'}">${t.activo ? 'ACTIVO' : 'INACTIVO'}</span></td>
            </tr>
        `).join('');
    }
};

window.DespachoRenderer = DespachoRenderer;
