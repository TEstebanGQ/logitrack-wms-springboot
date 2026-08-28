/**
 * Servicio de Ajustes de Inventario y Mermas (API)
 */
import { httpClient } from './http.js';

export const ajusteService = {
  listar: async () => httpClient.get('/ajustes'),
  obtenerPorId: async (id) => httpClient.get(`/ajustes/${id}`),
  listarPorBodega: async (bodegaId) => httpClient.get(`/ajustes/bodega/${bodegaId}`),
  listarPorProducto: async (productoId) => httpClient.get(`/ajustes/producto/${productoId}`),
  listarPorTipo: async (tipo) => httpClient.get(`/ajustes/tipo/${tipo}`),
  crear: async (datos) => httpClient.post('/ajustes', datos)
};
