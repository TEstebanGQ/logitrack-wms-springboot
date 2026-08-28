/* ==========================================
   LogiTrack S.A. - Movimiento Controller Module
   ========================================== */

const MovimientoModuleController = {
    allProductosCache: [],

    async load(filtroTipo = null) {
        try {
            let movimientos;
            if (filtroTipo) {
                movimientos = await MovimientoService.getByTipo(filtroTipo);
            } else {
                movimientos = await MovimientoService.getAll();
            }
            MovimientoRenderer.renderTable(movimientos);
        } catch (err) {
            console.error('Error cargando movimientos:', err);
            Toast.error('Error al cargar movimientos');
        }
    },

    async loadByFechas(inicioStr, finStr) {
        try {
            if (!inicioStr || !finStr) {
                Toast.error('Por favor selecciona la fecha de inicio y fin');
                return;
            }
            const inicioIso = `${inicioStr}T00:00:00`;
            const finIso = `${finStr}T23:59:59`;
            const movimientos = await MovimientoService.getByFechas(inicioIso, finIso);
            MovimientoRenderer.renderTable(movimientos);
        } catch (err) {
            console.error('Error filtrando movimientos por fecha:', err);
            Toast.error('Error al filtrar movimientos por fecha');
        }
    },

    async registrar(formData) {
        // Validaciones cliente antes de enviar
        if (formData.tipoMovimiento === 'TRANSFERENCIA') {
            if (!formData.bodegaOrigenId || !formData.bodegaDestinoId) {
                Toast.error('La transferencia requiere especificar bodega origen y bodega destino');
                return;
            }
            if (formData.bodegaOrigenId === formData.bodegaDestinoId) {
                Toast.error('La bodega origen y la bodega destino no pueden ser la misma');
                return;
            }
        } else if (formData.tipoMovimiento === 'SALIDA') {
            if (!formData.bodegaOrigenId) {
                Toast.error('La salida requiere especificar una bodega origen');
                return;
            }
        } else if (formData.tipoMovimiento === 'ENTRADA') {
            if (!formData.bodegaDestinoId) {
                Toast.error('La entrada requiere especificar una bodega destino');
                return;
            }
        }

        try {
            await MovimientoService.registrar(formData);
            Toast.success('Movimiento de inventario registrado con éxito');
            App.closeModal('modal-movimiento');
            this.load();
            // Refrescar vistas globales (productos, bodegas, dashboard)
            App.loadDashboardData();
            if (window.ProductoModuleController) window.ProductoModuleController.load();
            if (window.BodegaModuleController) window.BodegaModuleController.load();
        } catch (err) {
            Toast.error(err.message || 'Error al registrar el movimiento');
        }
    },

    async populateSelects() {
        try {
            const [bodegas, productos, proveedores, clientes] = await Promise.all([
                BodegaService.getAll(true).catch(() => []),
                ProductoService.getAll().catch(() => []),
                ProveedorService.getAll(true).catch(() => []),
                ClienteService.getAll(true).catch(() => [])
            ]);

            this.allProductosCache = productos || [];

            const tipoSelect = document.getElementById('mov-tipo');
            const origenSelect = document.getElementById('mov-origen');
            const destinoSelect = document.getElementById('mov-destino');
            const proveedorSelect = document.getElementById('mov-proveedor');
            const clienteSelect = document.getElementById('mov-cliente');

            const bodegaOpts = bodegas.map(b => `<option value="${b.id}">${b.nombre} (${b.ubicacion})</option>`).join('');

            if (origenSelect) {
                origenSelect.innerHTML = `<option value="">-- Seleccionar Bodega Origen --</option>` + bodegaOpts;
                origenSelect.onchange = () => this.onBodegaOrigenChange();
            }
            if (destinoSelect) {
                destinoSelect.innerHTML = `<option value="">-- Seleccionar Bodega Destino --</option>` + bodegaOpts;
            }

            if (proveedorSelect) {
                const provOpts = proveedores.map(p => `<option value="${p.id}">${p.nombre} ${p.ruc ? `(${p.ruc})` : ''}</option>`).join('');
                proveedorSelect.innerHTML = `<option value="">-- Seleccionar Proveedor (Opcional) --</option>` + provOpts;
            }

            if (clienteSelect) {
                const cliOpts = clientes.map(c => `<option value="${c.id}">${c.nombre} ${c.ruc ? `(${c.ruc})` : ''}</option>`).join('');
                clienteSelect.innerHTML = `<option value="">-- Seleccionar Cliente Receptor (Opcional) --</option>` + cliOpts;
            }

            if (tipoSelect) {
                tipoSelect.innerHTML = `
                    <option value="ENTRADA">ENTRADA</option>
                    <option value="SALIDA">SALIDA</option>
                    <option value="TRANSFERENCIA">TRANSFERENCIA</option>
                `;
                tipoSelect.onchange = () => this.onTipoChange();
            }

            this.onTipoChange();
            this.updateProductosSelect(this.allProductosCache);
        } catch (err) {
            console.error('Error cargando selects para movimiento:', err);
        }
    },

    onTipoChange() {
        const tipo = document.getElementById('mov-tipo')?.value;
        const origenGroup = document.getElementById('mov-origen-group') || document.getElementById('mov-origen')?.closest('.form-group');
        const destinoGroup = document.getElementById('mov-destino-group') || document.getElementById('mov-destino')?.closest('.form-group');
        const provGroup = document.getElementById('mov-proveedor-group');
        const cliGroup = document.getElementById('mov-cliente-group');

        if (tipo === 'ENTRADA') {
            if (origenGroup) origenGroup.style.display = 'none';
            if (destinoGroup) destinoGroup.style.display = 'block';
            if (provGroup) provGroup.style.display = 'block';
            if (cliGroup) cliGroup.style.display = 'none';
            this.updateProductosSelect(this.allProductosCache);
        } else if (tipo === 'SALIDA') {
            if (origenGroup) origenGroup.style.display = 'block';
            if (destinoGroup) destinoGroup.style.display = 'none';
            if (provGroup) provGroup.style.display = 'none';
            if (cliGroup) cliGroup.style.display = 'block';
            this.onBodegaOrigenChange();
        } else if (tipo === 'TRANSFERENCIA') {
            if (origenGroup) origenGroup.style.display = 'block';
            if (destinoGroup) destinoGroup.style.display = 'block';
            if (provGroup) provGroup.style.display = 'none';
            if (cliGroup) cliGroup.style.display = 'none';
            this.onBodegaOrigenChange();
        }
    },

    async onBodegaOrigenChange() {
        const bodegaOrigenId = document.getElementById('mov-origen')?.value;
        if (!bodegaOrigenId) {
            this.updateProductosSelect(this.allProductosCache);
            return;
        }

        try {
            const inventario = await BodegaService.getInventario(bodegaOrigenId);
            const mapInventario = new Map();
            (inventario || []).forEach(inv => {
                mapInventario.set(inv.productoId, inv.stockActual);
            });

            const productosConStock = this.allProductosCache.map(p => {
                const stockEnBodega = mapInventario.has(p.id) ? mapInventario.get(p.id) : 0;
                return {
                    ...p,
                    stockEnBodega: stockEnBodega
                };
            });

            this.updateProductosSelect(productosConStock, true);
        } catch (e) {
            console.error('Error consultando inventario por bodega:', e);
            this.updateProductosSelect(this.allProductosCache);
        }
    },

    updateProductosSelect(productos, porBodega = false) {
        const prodSelect = document.getElementById('mov-producto');
        if (!prodSelect) return;

        const prodOpts = productos.map(p => {
            if (porBodega) {
                const stockVal = p.stockEnBodega || 0;
                const textStatus = stockVal > 0 ? `${stockVal} u. disponibles en bodega` : '⚠️ Sin stock en esta bodega';
                return `<option value="${p.id}">${p.nombre} — [${textStatus}]</option>`;
            } else {
                return `<option value="${p.id}">${p.nombre} (Stock Global: ${p.stock} u.)</option>`;
            }
        }).join('');

        prodSelect.innerHTML = prodOpts || '<option value="">Sin productos disponibles</option>';
    }
};

window.MovimientoModuleController = MovimientoModuleController;
