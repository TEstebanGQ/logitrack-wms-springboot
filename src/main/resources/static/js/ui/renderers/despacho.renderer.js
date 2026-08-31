/* ==========================================
   LogiTrack S.A. - Despacho UI Renderer
   ========================================== */

const DespachoRenderer = {
    renderGuias(guias) {
        const list = Array.isArray(guias) ? guias : (guias && Array.isArray(guias.content) ? guias.content : []);

        const activas = list.filter(g => g.estadoEnvio !== 'ENTREGADO' && g.estadoEnvio !== 'DEVUELTO');
        const entregadas = list.filter(g => g.estadoEnvio === 'ENTREGADO' || g.estadoEnvio === 'DEVUELTO');

        // Contadores
        const badgeActivas = document.getElementById('contador-guias-activas');
        if (badgeActivas) badgeActivas.innerText = `${activas.length} en ruta`;

        const badgeHistorial = document.getElementById('contador-guias-historial');
        if (badgeHistorial) badgeHistorial.innerText = `${entregadas.length} entregadas`;

        const currentUser = typeof AuthService !== 'undefined' ? AuthService.getCurrentUser() : null;
        const canDeliver = currentUser && (currentUser.rol === 'ADMIN' || currentUser.rol === 'SUPERVISOR' || currentUser.rol === 'EMPLEADO');

        // 1. Render Guías en Ruta (Activas)
        const tbodyActivas = document.getElementById('tabla-guias-activas-body');
        if (tbodyActivas) {
            if (activas.length === 0) {
                tbodyActivas.innerHTML = `<tr><td colspan="8" class="text-center py-4" style="color:var(--text-muted);">No hay guías de despacho en tránsito pendientes de entrega.</td></tr>`;
            } else {
                tbodyActivas.innerHTML = activas.map(g => {
                    let badgeClass = 'badge-warning';
                    if (g.estadoEnvio === 'CON_NOVEDAD') badgeClass = 'badge-danger';

                    const fecha = g.fechaDespacho ? new Date(g.fechaDespacho).toLocaleDateString() : '-';

                    return `
                        <tr>
                            <td style="font-family:var(--font-mono); font-weight:600; color:var(--accent);">${g.numeroGuia}</td>
                            <td><strong style="color:var(--text-bright); font-family:var(--font-mono);">${g.codigoPedido || 'PED-' + g.pedidoId}</strong></td>
                            <td><strong>${g.clienteNombre || '-'}</strong></td>
                            <td>${g.transportadoraNombre}</td>
                            <td><small>${g.conductorNombre || '-'}<br><span style="color:var(--text-muted);">${g.placaVehiculo || ''}</span></small></td>
                            <td style="font-size:12px;">${fecha}</td>
                            <td><span class="badge ${badgeClass}">${g.estadoEnvio}</span></td>
                            <td>
                                ${canDeliver ? `
                                    <button class="btn btn-sm btn-success" onclick="DespachoController.marcarEntregado(${g.id})">
                                        Entregado
                                    </button>
                                ` : '<span style="color:var(--text-muted); font-size:12px;">Lectura</span>'}
                            </td>
                        </tr>
                    `;
                }).join('');
            }
        }

        // 2. Render Historial de Guías Entregadas
        const tbodyHistorial = document.getElementById('tabla-guias-historial-body');
        if (tbodyHistorial) {
            if (entregadas.length === 0) {
                tbodyHistorial.innerHTML = `<tr><td colspan="8" class="text-center py-4" style="color:var(--text-muted);">No hay guías entregadas en el historial.</td></tr>`;
            } else {
                tbodyHistorial.innerHTML = entregadas.map(g => {
                    const fecha = g.fechaDespacho ? new Date(g.fechaDespacho).toLocaleDateString() : '-';

                    return `
                        <tr>
                            <td style="font-family:var(--font-mono); font-weight:600; color:var(--text-muted);">${g.numeroGuia}</td>
                            <td><strong style="color:var(--text-bright); font-family:var(--font-mono);">${g.codigoPedido || 'PED-' + g.pedidoId}</strong></td>
                            <td><strong>${g.clienteNombre || '-'}</strong></td>
                            <td>${g.transportadoraNombre}</td>
                            <td><small>${g.conductorNombre || '-'}<br><span style="color:var(--text-muted);">${g.placaVehiculo || ''}</span></small></td>
                            <td style="font-size:12px;">${fecha}</td>
                            <td><span class="badge badge-success">${g.estadoEnvio}</span></td>
                            <td>
                                <span class="badge badge-success">
                                    Entregado al Cliente
                                </span>
                            </td>
                        </tr>
                    `;
                }).join('');
            }
        }
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
