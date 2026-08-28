/**
 * Servicio de Órdenes de Compra (API)
 */
import { httpClient } from './http.js';

export const ordenCompraService = {
  listar: async () => httpClient.get('/ordenes-compra'),
  obtenerPorId: async (id) => httpClient.get(`/ordenes-compra/${id}`),
  listarPorProveedor: async (proveedorId) => httpClient.get(`/ordenes-compra/proveedor/${proveedorId}`),
  listarPorEstado: async (estado) => httpClient.get(`/ordenes-compra/estado/${estado}`),
  crear: async (datos) => httpClient.post('/ordenes-compra', datos),
  aprobar: async (id) => httpClient.put(`/ordenes-compra/${id}/aprobar`, {}),
  cancelar: async (id, motivo) => httpClient.put(`/ordenes-compra/${id}/cancelar`, { motivo }),
  recibir: async (id) => httpClient.put(`/ordenes-compra/${id}/recibir`, {})
};
