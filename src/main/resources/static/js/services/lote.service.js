/**
 * Servicio de Lotes y Vencimientos (API)
 */
import { httpClient } from './http.js';

export const loteService = {
  listar: async () => httpClient.get('/lotes'),
  obtenerPorId: async (id) => httpClient.get(`/lotes/${id}`),
  listarPorProducto: async (productoId) => httpClient.get(`/lotes/producto/${productoId}`),
  listarPorBodega: async (bodegaId) => httpClient.get(`/lotes/bodega/${bodegaId}`),
  listarFEFO: async (productoId, bodegaId) => httpClient.get(`/lotes/fefo?productoId=${productoId}&bodegaId=${bodegaId}`),
  listarProximosVencer: async (dias = 30) => httpClient.get(`/lotes/proximos-vencer?dias=${dias}`),
  crear: async (datos) => httpClient.post('/lotes', datos),
  actualizarEstado: async (id, estado) => httpClient.put(`/lotes/${id}/estado?estado=${estado}`, {})
};
