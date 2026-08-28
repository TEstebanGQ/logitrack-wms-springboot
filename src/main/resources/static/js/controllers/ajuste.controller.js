/**
 * Controlador de Vista de Ajustes
 */
import { ajusteService } from '../services/ajuste.service.js';
import { bodegaService } from '../services/bodega.service.js';
import { ajusteRenderer } from '../ui/renderers/ajuste.renderer.js';
import { toast } from '../ui/toast.js';

export const ajusteController = {
  init: async () => {
    try {
      const [ajustes, bodegas] = await Promise.all([
        ajusteService.listar(),
        bodegaService.listar()
      ]);

      ajusteRenderer.renderTabla(ajustes);

      const selectBodega = document.getElementById('filtro-ajuste-bodega');
      if (selectBodega) {
        selectBodega.innerHTML = '<option value="">Todas las bodegas</option>' +
          bodegas.map(b => `<option value="${b.id}">${b.nombre}</option>`).join('');
      }

      ajusteController.setupListeners();
    } catch (error) {
      toast.error('Error al cargar la lista de ajustes de inventario');
    }
  },

  setupListeners: () => {
    const filtroBodega = document.getElementById('filtro-ajuste-bodega');
    const filtroTipo = document.getElementById('filtro-ajuste-tipo');

    const aplicarFiltros = async () => {
      try {
        const bodegaId = filtroBodega?.value;
        const tipo = filtroTipo?.value;

        if (bodegaId) {
          const res = await ajusteService.listarPorBodega(bodegaId);
          ajusteRenderer.renderTabla(tipo ? res.filter(a => a.tipoAjuste === tipo) : res);
        } else if (tipo) {
          const res = await ajusteService.listarPorTipo(tipo);
          ajusteRenderer.renderTabla(res);
        } else {
          const res = await ajusteService.listar();
          ajusteRenderer.renderTabla(res);
        }
      } catch (error) {
        toast.error('Error al filtrar ajustes');
      }
    };

    filtroBodega?.addEventListener('change', aplicarFiltros);
    filtroTipo?.addEventListener('change', aplicarFiltros);
  }
};
