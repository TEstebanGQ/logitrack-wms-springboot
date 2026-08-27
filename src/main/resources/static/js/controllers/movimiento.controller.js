/* ==========================================
   LogiTrack S.A. - Movimiento Controller Module
   ========================================== */

const MovimientoModuleController = {
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
        try {
            await MovimientoService.registrar(formData);
            Toast.success('Movimiento de inventario registrado con éxito');
            App.closeModal('modal-movimiento');
            this.load();
        } catch (err) {
            Toast.error(err.message || 'Error al registrar el movimiento');
        }
    },

    async populateSelects() {
        try {
            const [bodegas, productos] = await Promise.all([
                BodegaService.getAll(true),
                ProductoService.getAll()
            ]);

            const origenSelect = document.getElementById('mov-origen');
            const destinoSelect = document.getElementById('mov-destino');
            const prodSelect = document.getElementById('mov-producto');

            const bodegaOpts = bodegas.map(b => `<option value="${b.id}">${b.nombre} (${b.ubicacion})</option>`).join('');
            const prodOpts = productos.map(p => `<option value="${p.id}">${p.nombre} (Stock: ${p.stock})</option>`).join('');

            if (origenSelect) origenSelect.innerHTML = `<option value="">-- Ninguna --</option>` + bodegaOpts;
            if (destinoSelect) destinoSelect.innerHTML = `<option value="">-- Ninguna --</option>` + bodegaOpts;
            if (prodSelect) prodSelect.innerHTML = prodOpts;
        } catch (err) {
            console.error('Error cargando selects para movimiento:', err);
        }
    }
};

window.MovimientoModuleController = MovimientoModuleController;
