package com.proyecto.proyectoSpringBoot.service.interfaces;

import com.proyecto.proyectoSpringBoot.dto.request.CrearProductoRequest;
import com.proyecto.proyectoSpringBoot.dto.response.ProductoResponse;

import java.util.List;

public interface IProductoService {
    ProductoResponse crear(CrearProductoRequest request);
    ProductoResponse obtenerPorId(Long id);
    List<ProductoResponse> listarTodos();
    List<ProductoResponse> listarActivos();
    List<ProductoResponse> listarConStockBajo(int umbral);
    List<ProductoResponse> listarPorCategoria(String categoria);
    ProductoResponse actualizar(Long id, CrearProductoRequest request);
    void eliminar(Long id);
}
