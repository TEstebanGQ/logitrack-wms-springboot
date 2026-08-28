package com.proyecto.proyectoSpringBoot.service.interfaces;

import com.proyecto.proyectoSpringBoot.dto.request.ProductoSerieRequest;
import com.proyecto.proyectoSpringBoot.dto.response.ProductoSerieResponse;
import com.proyecto.proyectoSpringBoot.model.enums.EstadoSerie;

import java.util.List;

public interface IProductoSerieService {
    List<ProductoSerieResponse> listarTodas();
    List<ProductoSerieResponse> listarPorProducto(Long productoId);
    List<ProductoSerieResponse> listarPorBodega(Long bodegaId);
    List<ProductoSerieResponse> listarPorEstado(EstadoSerie estado);
    ProductoSerieResponse obtenerPorId(Long id);
    ProductoSerieResponse registrar(ProductoSerieRequest request);
    ProductoSerieResponse actualizarEstado(Long id, EstadoSerie nuevoEstado, String observaciones);
    void eliminar(Long id);
}
