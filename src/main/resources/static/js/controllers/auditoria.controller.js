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

        const prevEl = document.getElementById('aud-json-anteriores');
        const newEl = document.getElementById('aud-json-nuevos');
        const titleEl = document.getElementById('aud-modal-title');

        if (titleEl) titleEl.innerText = `Auditoría #${item.id} - ${item.entidad} (${item.tipoOperacion})`;

        try {
            prevEl.innerText = item.valoresAnteriores ? JSON.stringify(JSON.parse(item.valoresAnteriores), null, 2) : '(Sin valores previos)';
        } catch(e) {
            prevEl.innerText = item.valoresAnteriores || '(Sin valores previos)';
        }

        try {
            newEl.innerText = item.valoresNuevos ? JSON.stringify(JSON.parse(item.valoresNuevos), null, 2) : '(Sin valores nuevos)';
        } catch(e) {
            newEl.innerText = item.valoresNuevos || '(Sin valores nuevos)';
        }

        App.openModal('modal-auditoria');
    }
};

window.AuditoriaModuleController = AuditoriaModuleController;
