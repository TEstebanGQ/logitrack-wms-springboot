package com.proyecto.proyectoSpringBoot.mapper;

import com.proyecto.proyectoSpringBoot.dto.response.ProductoSerieResponse;
import com.proyecto.proyectoSpringBoot.model.entity.ProductoSerie;
import org.springframework.stereotype.Component;

@Component
public class ProductoSerieMapper {

    public ProductoSerieResponse toResponse(ProductoSerie s) {
        if (s == null) return null;
        return ProductoSerieResponse.builder()
                .id(s.getId())
                .numeroSerie(s.getNumeroSerie())
                .productoId(s.getProducto() != null ? s.getProducto().getId() : null)
                .productoNombre(s.getProducto() != null ? s.getProducto().getNombre() : null)
                .bodegaId(s.getBodega() != null ? s.getBodega().getId() : null)
                .bodegaNombre(s.getBodega() != null ? s.getBodega().getNombre() : null)
                .ubicacionId(s.getUbicacion() != null ? s.getUbicacion().getId() : null)
                .codigoUbicacion(s.getUbicacion() != null ? s.getUbicacion().getCodigoUbicacion() : null)
                .estado(s.getEstado())
                .fechaIngreso(s.getFechaIngreso())
                .fechaDespacho(s.getFechaDespacho())
                .observaciones(s.getObservaciones())
                .build();
    }
}
