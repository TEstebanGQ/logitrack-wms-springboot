/* ==========================================
   LogiTrack S.A. - Despacho Module Controller
   ========================================== */

const DespachoController = {
    guiasList: [],
    transportadorasList: [],

    async load() {
        try {
            const [guias, transps] = await Promise.all([
                DespachoService.getGuias().catch(() => []),
                DespachoService.getTransportadoras().catch(() => [])
            ]);
            this.guiasList = guias;
            this.transportadorasList = transps;

            DespachoRenderer.renderGuias(guias);
            DespachoRenderer.renderTransportadoras(transps);
        } catch (err) {
            console.error('Error cargando despachos:', err);
            Toast.error('Error al cargar datos de despacho');
        }
    },

    async abrirModalGuia() {
        const [pedidos, transps] = await Promise.all([
            typeof PedidoService !== 'undefined' ? PedidoService.getAll().catch(() => []) : [],
            DespachoService.getTransportadoras().catch(() => [])
        ]);

        const pedidosList = Array.isArray(pedidos) ? pedidos : (pedidos && Array.isArray(pedidos.content) ? pedidos.content : []);
        const transpsList = Array.isArray(transps) ? transps : (transps && Array.isArray(transps.content) ? transps.content : []);

        const selPedido = document.getElementById('guia-pedido');
        const selTransp = document.getElementById('guia-transportadora');

        // Filtrar estrictamente los pedidos que están en estado DESPACHADO
        const pedidosDespachados = pedidosList.filter(p => p.estado === 'DESPACHADO');

        if (selPedido) {
            if (pedidosDespachados.length === 0) {
                selPedido.innerHTML = '<option value="">-- No hay pedidos con estado DESPACHADO disponibles --</option>';
            } else {
                selPedido.innerHTML = pedidosDespachados.map(p => `<option value="${p.id}">${p.codigoPedido} - ${p.clienteNombre} (${p.estado})</option>`).join('');
            }
        }
        if (selTransp) selTransp.innerHTML = transpsList.map(t => `<option value="${t.id}">${t.nombre} (${t.tipoServicio})</option>`).join('');

        const modal = document.getElementById('modal-guia');
        if (modal) modal.style.display = 'flex';
    },


    cerrarModalGuia() {
        const modal = document.getElementById('modal-guia');
        if (modal) modal.style.display = 'none';
        document.getElementById('form-guia')?.reset();
    },

    async guardarGuia(e) {
        e.preventDefault();
        try {
            const data = {
                numeroGuia: document.getElementById('guia-numero').value,
                pedidoId: parseInt(document.getElementById('guia-pedido').value),
                transportadoraId: parseInt(document.getElementById('guia-transportadora').value),
                conductorNombre: document.getElementById('guia-conductor').value,
                placaVehiculo: document.getElementById('guia-placa').value,
                costoFlete: parseFloat(document.getElementById('guia-flete').value || 0)
            };

            await DespachoService.crearGuia(data);
            Toast.success('Guía de despacho generada exitosamente.');
            this.cerrarModalGuia();
            this.load();
        } catch (err) {
            Toast.error(err.response?.data?.message || 'Error al generar guía de despacho');
        }
    },

    async marcarEntregado(id) {
        try {
            await DespachoService.actualizarEstadoGuia(id, 'ENTREGADO', 'Entrega confirmada con firma');
            Toast.success('Estado actualizado a ENTREGADO.');
            this.load();
        } catch (err) {
            Toast.error('Error al actualizar estado');
        }
    },

    abrirModalTransportadora() {
        const modal = document.getElementById('modal-transp');
        if (modal) modal.style.display = 'flex';
    },

    cerrarModalTransportadora() {
        const modal = document.getElementById('modal-transp');
        if (modal) modal.style.display = 'none';
        document.getElementById('form-transp')?.reset();
    },

    async guardarTransportadora(e) {
        e.preventDefault();
        try {
            const data = {
                nombre: document.getElementById('transp-nombre').value,
                rucNit: document.getElementById('transp-ruc').value,
                tipoServicio: document.getElementById('transp-tipo').value,
                telefono: document.getElementById('transp-telefono').value,
                email: document.getElementById('transp-email').value
            };

            await DespachoService.crearTransportadora(data);
            Toast.success('Empresa transportadora guardada.');
            this.cerrarModalTransportadora();
            this.load();
        } catch (err) {
            Toast.error(err.response?.data?.message || 'Error al guardar transportadora');
        }
    }
};

window.DespachoController = DespachoController;
