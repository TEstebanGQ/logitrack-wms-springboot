/**
 * Controlador de Vista de Órdenes de Compra
 */
import { ordenCompraService } from '../services/orden-compra.service.js';
import { ordenCompraRenderer } from '../ui/renderers/orden-compra.renderer.js';
import { toast } from '../ui/toast.js';

export const ordenCompraController = {
  init: async () => {
    try {
      const ordenes = await ordenCompraService.listar();
      ordenCompraRenderer.renderTabla(ordenes);
      ordenCompraController.setupListeners();
    } catch (error) {
      toast.error('Error al cargar las órdenes de compra');
    }
  },

  setupListeners: () => {
    const filtroEstado = document.getElementById('filtro-orden-estado');
    filtroEstado?.addEventListener('change', async (e) => {
      try {
        const estado = e.target.value;
        const res = estado ? await ordenCompraService.listarPorEstado(estado) : await ordenCompraService.listar();
        ordenCompraRenderer.renderTabla(res);
      } catch (error) {
        toast.error('Error al filtrar órdenes');
      }
    });

    const tbody = document.getElementById('tabla-ordenes-body');
    tbody?.addEventListener('click', async (e) => {
      const btnAprobar = e.target.closest('.btn-aprobar-orden');
      const btnCancelar = e.target.closest('.btn-cancelar-orden');
      const btnRecibir = e.target.closest('.btn-recibir-orden');

      if (btnAprobar) {
        const id = btnAprobar.dataset.id;
        if (confirm('¿Confirmas la aprobación de esta orden de compra?')) {
          try {
            await ordenCompraService.aprobar(id);
            toast.success('Orden de compra aprobada exitosamente');
            ordenCompraController.init();
          } catch (err) {
            toast.error(err.message || 'Error al aprobar orden');
          }
        }
      }

      if (btnCancelar) {
        const id = btnCancelar.dataset.id;
        const motivo = prompt('Ingresa el motivo de cancelación:');
        if (motivo !== null) {
          try {
            await ordenCompraService.cancelar(id, motivo);
            toast.success('Orden de compra cancelada');
            ordenCompraController.init();
          } catch (err) {
            toast.error(err.message || 'Error al cancelar orden');
          }
        }
      }

      if (btnRecibir) {
        const id = btnRecibir.dataset.id;
        if (confirm('¿Deseas recibir la mercancía e ingresar automáticamente el inventario a bodega?')) {
          try {
            await ordenCompraService.recibir(id);
            toast.success('Mercancía recibida e inventario actualizado');
            ordenCompraController.init();
          } catch (err) {
            toast.error(err.message || 'Error al recibir orden');
          }
        }
      }
    });
  }
};
