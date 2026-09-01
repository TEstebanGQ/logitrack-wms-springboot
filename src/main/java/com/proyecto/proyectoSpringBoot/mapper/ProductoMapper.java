package com.proyecto.proyectoSpringBoot.mapper;

import com.proyecto.proyectoSpringBoot.dto.request.CrearProductoRequest;
import com.proyecto.proyectoSpringBoot.dto.response.ProductoResponse;
import com.proyecto.proyectoSpringBoot.model.entity.Producto;
import org.springframework.stereotype.Component;

@Component
public class ProductoMapper {

    public Producto toEntity(CrearProductoRequest req) {
        return Producto.builder()
                .nombre(req.getNombre())
                // .categoria(req.getCategoriaId()) // will be set in service
                .stock(req.getStock() != null ? req.getStock() : 0)
                .stockMinimo(req.getStockMinimo() != null ? req.getStockMinimo() : 10)
                .precio(req.getPrecio())
                .descripcion(req.getDescripcion())
                .activo(true)
                .build();
    }

    public ProductoResponse toResponse(Producto p) {
        return ProductoResponse.builder()
                .id(p.getId())
                .nombre(p.getNombre())
                .categoriaId(p.getCategoria() != null ? p.getCategoria().getId() : null)
                .categoriaNombre(p.getCategoria() != null ? p.getCategoria().getNombre() : null)
                .stock(p.getStock())
                .stockMinimo(p.getStockMinimo())
                .precio(p.getPrecio())
                .descripcion(p.getDescripcion())
                .activo(p.isActivo())
                .createdAt(p.getCreatedAt())
                .build();
    }

    public void updateEntity(Producto producto, CrearProductoRequest req) {
        producto.setNombre(req.getNombre());
        producto.setStock(req.getStock() != null ? req.getStock() : producto.getStock());
        if (req.getStockMinimo() != null) {
            producto.setStockMinimo(req.getStockMinimo());
        }
        producto.setPrecio(req.getPrecio());
        producto.setDescripcion(req.getDescripcion());
    }
}
