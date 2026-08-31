/* ==========================================
   LogiTrack S.A. - Pedido Module Controller
   ========================================== */

const PedidoController = {
    pedidosList: [],

    async load() {
        try {
            this.pedidosList = await PedidoService.getAll().catch(() => []);
            this.aplicarFiltros();
            this.initFilterListeners();
        } catch (err) {
            console.error('Error al cargar pedidos:', err);
            Toast.error('Error al cargar la lista de pedidos');
        }
    },

    aplicarFiltros() {
        const search = (document.getElementById('filtro-ped-search')?.value || '').toLowerCase().trim();
        const estado = document.getElementById('filtro-ped-estado')?.value || '';

        let filtrados = this.pedidosList;
        if (search) {
            filtrados = filtrados.filter(p =>
                (p.codigoPedido && p.codigoPedido.toLowerCase().includes(search)) ||
                (p.clienteNombre && p.clienteNombre.toLowerCase().includes(search))
            );
        }
        if (estado) {
            filtrados = filtrados.filter(p => p.estado === estado);
        }

        PedidoRenderer.renderTable(filtrados);
    },

    initFilterListeners() {
        document.getElementById('filtro-ped-search')?.addEventListener('input', () => this.aplicarFiltros());
        document.getElementById('filtro-ped-estado')?.addEventListener('change', () => this.aplicarFiltros());
    },

    async abrirModalCrear() {
        const [clientes, bodegas, productos] = await Promise.all([
            typeof ClienteService !== 'undefined' ? ClienteService.getAll().catch(() => []) : [],
            typeof BodegaService !== 'undefined' ? BodegaService.getAll().catch(() => []) : [],
            typeof ProductoService !== 'undefined' ? ProductoService.getAll().catch(() => []) : []
        ]);

        const selCliente = document.getElementById('ped-cliente');
        const selBodega = document.getElementById('ped-bodega');
        const selProd = document.getElementById('ped-producto');

        if (selCliente) selCliente.innerHTML = clientes.map(c => `<option value="${c.id}">${c.nombre}</option>`).join('');
        if (selBodega) selBodega.innerHTML = bodegas.map(b => `<option value="${b.id}">${b.nombre}</option>`).join('');
        if (selProd) selProd.innerHTML = productos.map(p => `<option value="${p.id}">${p.nombre} (Stock: ${p.stock})</option>`).join('');

        const modal = document.getElementById('modal-pedido');
        if (modal) modal.style.display = 'flex';
    },

    cerrarModal() {
        const modal = document.getElementById('modal-pedido');
        if (modal) modal.style.display = 'none';
        document.getElementById('form-pedido')?.reset();
    },

    async guardar(e) {
        e.preventDefault();
        try {
            const data = {
                clienteId: parseInt(document.getElementById('ped-cliente').value),
                bodegaOrigenId: parseInt(document.getElementById('ped-bodega').value),
                fechaCompromiso: document.getElementById('ped-fecha-compromiso').value || null,
                direccionEntrega: document.getElementById('ped-direccion').value,
                observaciones: document.getElementById('ped-obs').value,
                detalles: [
                    {
                        productoId: parseInt(document.getElementById('ped-producto').value),
                        cantidadSolicitada: parseInt(document.getElementById('ped-cantidad').value)
                    }
                ]
            };

            await PedidoService.create(data);
            Toast.success('Pedido registrado con éxito. Se generaron órdenes de picking.');
            this.cerrarModal();
            this.load();
        } catch (err) {
            console.error('Error al crear pedido:', err);
            Toast.error(err.response?.data?.message || 'Error al registrar pedido');
        }
    },

    async despachar(id) {
        const ok = await ConfirmDialog.show({ title: 'Despachar Pedido', message: '¿Deseas confirmar el despacho de este pedido? Se descontará el inventario y se generará movimiento de salida.', type: 'info', confirmText: 'Sí, Despachar' });
        if (!ok) return;
        try {
            await PedidoService.despachar(id);
            Toast.success('Pedido despachado con éxito.');
            this.load();
        } catch (err) {
            Toast.error(err.response?.data?.message || 'Error al despachar pedido');
        }
    },

    async cancelar(id) {
        const motivo = await ConfirmDialog.prompt({ title: 'Cancelar Pedido Comercial', message: 'Ingresa el motivo de cancelación del pedido:', placeholder: 'Ej. Solicitud del cliente, falta de stock...' });
        if (motivo === null || motivo === '') return;
        try {
            await PedidoService.cancelar(id, motivo);
            Toast.success('Pedido cancelado.');
            this.load();
        } catch (err) {
            Toast.error('Error al cancelar pedido');
        }
    }
};

window.PedidoController = PedidoController;
