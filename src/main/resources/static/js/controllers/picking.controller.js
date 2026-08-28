/* ==========================================
   LogiTrack S.A. - Picking Module Controller
   ========================================== */

const PickingController = {
    tareasList: [],

    async load() {
        try {
            this.tareasList = await PickingService.getAll().catch(() => []);
            this.aplicarFiltros();
            this.initFilterListeners();
        } catch (err) {
            console.error('Error al cargar picking:', err);
            Toast.error('Error al cargar tareas de picking');
        }
    },

    aplicarFiltros() {
        const search = (document.getElementById('filtro-pck-search')?.value || '').toLowerCase().trim();
        const estado = document.getElementById('filtro-pck-estado')?.value || '';

        let filtrados = this.tareasList;
        if (search) {
            filtrados = filtrados.filter(t =>
                (t.codigoTarea && t.codigoTarea.toLowerCase().includes(search)) ||
                (t.codigoPedido && t.codigoPedido.toLowerCase().includes(search)) ||
                (t.productoNombre && t.productoNombre.toLowerCase().includes(search))
            );
        }
        if (estado) {
            filtrados = filtrados.filter(t => t.estado === estado);
        }

        PickingRenderer.renderTable(filtrados);
    },

    initFilterListeners() {
        document.getElementById('filtro-pck-search')?.addEventListener('input', () => this.aplicarFiltros());
        document.getElementById('filtro-pck-estado')?.addEventListener('change', () => this.aplicarFiltros());
    },

    abrirModalRecoleccion(id, prodNombre, cantReq, cantRec) {
        document.getElementById('pck-tarea-id').value = id;
        document.getElementById('pck-prod-nombre').value = prodNombre;
        document.getElementById('pck-cant-recogida').value = cantReq;
        document.getElementById('pck-cant-recogida').max = cantReq;
        document.getElementById('pck-cant-max-info').innerText = `Requerido total: ${cantReq} unidades (Actual recogido: ${cantRec})`;

        const modal = document.getElementById('modal-picking-accion');
        if (modal) modal.style.display = 'flex';
    },

    cerrarModal() {
        const modal = document.getElementById('modal-picking-accion');
        if (modal) modal.style.display = 'none';
        document.getElementById('form-picking-accion')?.reset();
    },

    async guardarAvance(e) {
        e.preventDefault();
        try {
            const id = parseInt(document.getElementById('pck-tarea-id').value);
            const cant = parseInt(document.getElementById('pck-cant-recogida').value);
            const notas = document.getElementById('pck-notas').value;

            await PickingService.recolectar(id, cant, notas);
            Toast.success('Avance de picking guardado.');
            this.cerrarModal();
            this.load();
        } catch (err) {
            Toast.error('Error al registrar recolección');
        }
    }
};

window.PickingController = PickingController;
