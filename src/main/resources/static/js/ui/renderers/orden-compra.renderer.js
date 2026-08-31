/* ==========================================
   LogiTrack S.A. - Renderizador UI de Órdenes de Compra
   ========================================== */

const OrdenCompraRenderer = {
    renderTabla(ordenes) {
        const tbodyActivas = document.getElementById('tabla-ordenes-body');
        const tbodyHistorial = document.getElementById('tabla-ordenes-historial-body');
        const badgeActivas = document.getElementById('badge-contador-activas');
        const badgeHistorial = document.getElementById('badge-contador-historial');

        const list = Array.isArray(ordenes) ? ordenes : (ordenes && Array.isArray(ordenes.content) ? ordenes.content : []);

        const activas = list.filter(o => o.estado === 'PENDIENTE' || o.estado === 'APROBADA');
        const historial = list.filter(o => o.estado === 'RECIBIDA' || o.estado === 'CANCELADA');

        if (badgeActivas) badgeActivas.innerText = `${activas.length} en proceso`;
        if (badgeHistorial) badgeHistorial.innerText = `${historial.length} archivadas`;

        // 1. Renderizar Órdenes Activas
        if (tbodyActivas) {
            if (activas.length === 0) {
                tbodyActivas.innerHTML = '<tr><td colspan="8" class="text-center py-4" style="color:var(--text-muted);">No hay órdenes pendientes por gestionar.</td></tr>';
            } else {
                tbodyActivas.innerHTML = activas.map(o => this.buildRow(o, true)).join('');
            }
        }

        // 2. Renderizar Historial de Órdenes Finalizadas
        if (tbodyHistorial) {
            if (historial.length === 0) {
                tbodyHistorial.innerHTML = '<tr><td colspan="8" class="text-center py-4" style="color:var(--text-muted);">No hay órdenes finalizadas en el historial.</td></tr>';
            } else {
                tbodyHistorial.innerHTML = historial.map(o => this.buildRow(o, false)).join('');
            }
        }
    },

    buildRow(o, esActiva) {
        const fechaSol = o.fechaSolicitud ? new Date(o.fechaSolicitud).toLocaleDateString() : 'N/A';
        const fechaEnt = o.fechaEntregaEsperada ? o.fechaEntregaEsperada : (o.estado === 'RECIBIDA' ? 'Recibida' : 'N/A');
        const totalFmt = new Intl.NumberFormat('es-CO', { style: 'currency', currency: 'COP', maximumFractionDigits: 0 }).format(o.totalEstimado || 0);

        let estadoBadge = '<span class="badge badge-warning">PENDIENTE</span>';
        if (o.estado === 'APROBADA') estadoBadge = '<span class="badge badge-info">APROBADA</span>';
        if (o.estado === 'RECIBIDA') estadoBadge = '<span class="badge badge-success">RECIBIDA</span>';
        if (o.estado === 'CANCELADA') estadoBadge = '<span class="badge badge-danger">CANCELADA</span>';

        let acciones = '';
        if (o.estado === 'PENDIENTE') {
            acciones = `
                <button class="btn btn-sm btn-info" style="padding: 3px 8px; font-size: 11px;" onclick="OrdenCompraController.aprobar(${o.id})" title="Aprobar Orden">Aprobar</button>
                <button class="btn btn-sm btn-danger" style="padding: 3px 8px; font-size: 11px;" onclick="OrdenCompraController.cancelar(${o.id})" title="Cancelar Orden">Cancelar</button>
            `;
        } else if (o.estado === 'APROBADA') {
            acciones = `
                <button class="btn btn-sm btn-primary" style="padding: 3px 10px; font-size: 11px; background:var(--accent); border-color:var(--accent);" onclick="OrdenCompraController.recibir(${o.id})">Recibir Mercancía</button>
            `;
        }

        acciones += `
            <button class="btn btn-sm btn-secondary" style="padding: 3px 8px; font-size: 11px;" onclick="OrdenCompraController.verDetalle(${o.id})" title="Ver Información Detallada">Detalle</button>
        `;

        return `
            <tr>
                <td><strong style="color:var(--text-bright); font-family:var(--font-mono);">${o.codigoOrden}</strong></td>
                <td>${fechaSol}</td>
                <td><strong>${o.proveedorNombre || 'N/A'}</strong></td>
                <td>${o.bodegaDestinoNombre || 'N/A'}</td>
                <td><strong style="color:var(--accent); font-family:var(--font-mono);">${totalFmt}</strong></td>
                <td>${estadoBadge}</td>
                <td>${fechaEnt}</td>
                <td><div class="table-actions">${acciones}</div></td>
            </tr>
        `;
    },

    renderModalDetalle(orden) {
        const codigoEl = document.getElementById('modal-doc-codigo');
        const estadoEl = document.getElementById('modal-doc-estado');
        const metaEl = document.getElementById('modal-doc-meta');
        const itemsBodyEl = document.getElementById('modal-doc-items-body');
        const obsWrap = document.getElementById('modal-doc-observaciones-wrap');
        const obsEl = document.getElementById('modal-doc-observaciones');

        if (codigoEl) codigoEl.innerText = `Orden de Compra: ${orden.codigoOrden || '#' + orden.id}`;

        if (estadoEl) {
            estadoEl.innerText = orden.estado;
            estadoEl.className = 'badge ' + (
                orden.estado === 'RECIBIDA' ? 'badge-success' :
                orden.estado === 'APROBADA' ? 'badge-info' :
                orden.estado === 'CANCELADA' ? 'badge-danger' : 'badge-warning'
            );
        }

        const totalFmt = new Intl.NumberFormat('es-CO', { style: 'currency', currency: 'COP', maximumFractionDigits: 0 }).format(orden.totalEstimado || 0);
        const fechaSol = orden.fechaSolicitud ? new Date(orden.fechaSolicitud).toLocaleString() : 'N/A';

        if (metaEl) {
            metaEl.innerHTML = `
                <div class="meta-item">
                    <label>Proveedor</label>
                    <span>${orden.proveedorNombre || 'N/A'}</span>
                </div>
                <div class="meta-item">
                    <label>Bodega Destino</label>
                    <span>${orden.bodegaDestinoNombre || 'N/A'}</span>
                </div>
                <div class="meta-item">
                    <label>Fecha de Solicitud</label>
                    <span>${fechaSol}</span>
                </div>
                <div class="meta-item">
                    <label>Entrega Esperada</label>
                    <span>${orden.fechaEntregaEsperada || 'No especificada'}</span>
                </div>
                <div class="meta-item">
                    <label>Solicitado Por</label>
                    <span>${orden.usuarioSolicitante || 'Operador'}</span>
                </div>
                <div class="meta-item">
                    <label>Total Estimado</label>
                    <span style="color:var(--accent); font-size:15px; font-family:var(--font-mono);">${totalFmt}</span>
                </div>
            `;
        }

        if (itemsBodyEl) {
            const detalles = orden.detalles || [];
            if (detalles.length === 0) {
                itemsBodyEl.innerHTML = '<tr><td colspan="4" class="text-center py-3" style="color:var(--text-muted);">Sin desglose de ítems registrado.</td></tr>';
            } else {
                itemsBodyEl.innerHTML = detalles.map(d => {
                    const precioFmt = new Intl.NumberFormat('es-CO', { style: 'currency', currency: 'COP', maximumFractionDigits: 0 }).format(d.precioUnitario || 0);
                    const subtotalFmt = new Intl.NumberFormat('es-CO', { style: 'currency', currency: 'COP', maximumFractionDigits: 0 }).format(d.subtotal || 0);
                    return `
                        <tr>
                            <td><strong>${d.productoNombre || 'Producto #' + d.productoId}</strong></td>
                            <td><span class="badge badge-secondary">${d.cantidad} unidades</span></td>
                            <td style="font-family:var(--font-mono);">${precioFmt}</td>
                            <td><strong style="color:var(--text-bright); font-family:var(--font-mono);">${subtotalFmt}</strong></td>
                        </tr>
                    `;
                }).join('');
            }
        }

        if (obsWrap && obsEl) {
            if (orden.observaciones && orden.observaciones.trim() !== '') {
                obsEl.innerText = orden.observaciones;
                obsWrap.style.display = 'block';
            } else {
                obsWrap.style.display = 'none';
            }
        }
    }
};

window.OrdenCompraRenderer = OrdenCompraRenderer;

