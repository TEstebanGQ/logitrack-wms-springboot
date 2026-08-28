/**
 * Controlador de Vista de Lotes y Vencimientos
 */
import { loteService } from '../services/lote.service.js';
import { bodegaService } from '../services/bodega.service.js';
import { loteRenderer } from '../ui/renderers/lote.renderer.js';
import { toast } from '../ui/toast.js';

export const loteController = {
  init: async () => {
    try {
      const [lotes, bodegas] = await Promise.all([
        loteService.listar(),
        bodegaService.listar()
      ]);

      loteRenderer.renderTabla(lotes);

      const selectBodega = document.getElementById('filtro-lote-bodega');
      if (selectBodega) {
        selectBodega.innerHTML = '<option value="">Todas las bodegas</option>' +
          bodegas.map(b => `<option value="${b.id}">${b.nombre}</option>`).join('');
      }

      loteController.setupListeners();
    } catch (error) {
      toast.error('Error al cargar los lotes');
    }
  },

  setupListeners: () => {
    const filtroBodega = document.getElementById('filtro-lote-bodega');
    filtroBodega?.addEventListener('change', async (e) => {
      try {
        const bodegaId = e.target.value;
        const res = bodegaId ? await loteService.listarPorBodega(bodegaId) : await loteService.listar();
        loteRenderer.renderTabla(res);
      } catch (error) {
        toast.error('Error al filtrar lotes');
      }
    });

    const btnProximos = document.getElementById('btn-ver-proximos-vencer');
    btnProximos?.addEventListener('click', async () => {
      try {
        const res = await loteService.listarProximosVencer(30);
        loteRenderer.renderTabla(res);
        toast.info(`Se encontraron ${res.length} lotes próximos a vencer`);
      } catch (error) {
        toast.error('Error al consultar lotes próximos a vencer');
      }
    });

    const tbody = document.getElementById('tabla-lotes-body');
    tbody?.addEventListener('click', async (e) => {
      const btnEstado = e.target.closest('.btn-cambiar-estado-lote');
      if (btnEstado) {
        const id = btnEstado.dataset.id;
        const nuevoEstado = prompt('Nuevo estado (DISPONIBLE, CUARENTENA, VENCIDO, AGOTADO):');
        if (nuevoEstado) {
          try {
            await loteService.actualizarEstado(id, nuevoEstado.toUpperCase());
            toast.success('Estado del lote actualizado');
            loteController.init();
          } catch (err) {
            toast.error(err.message || 'Error al actualizar estado del lote');
          }
        }
      }
    });
  }
};
