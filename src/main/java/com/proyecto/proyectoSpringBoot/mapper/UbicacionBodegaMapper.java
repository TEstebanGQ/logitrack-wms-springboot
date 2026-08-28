package com.proyecto.proyectoSpringBoot.mapper;

import com.proyecto.proyectoSpringBoot.dto.response.UbicacionBodegaResponse;
import com.proyecto.proyectoSpringBoot.model.entity.UbicacionBodega;
import org.springframework.stereotype.Component;

@Component
public class UbicacionBodegaMapper {

    public UbicacionBodegaResponse toResponse(UbicacionBodega entity) {
        if (entity == null) return null;

        return UbicacionBodegaResponse.builder()
                .id(entity.getId())
                .bodegaId(entity.getBodega() != null ? entity.getBodega().getId() : null)
                .bodegaNombre(entity.getBodega() != null ? entity.getBodega().getNombre() : null)
                .codigoUbicacion(entity.getCodigoUbicacion())
                .pasillo(entity.getPasillo())
                .estante(entity.getEstante())
                .nivel(entity.getNivel())
                .capacidadMax(entity.getCapacidadMax())
                .descripcion(entity.getDescripcion())
                .activo(entity.getActivo() != null ? entity.getActivo() : true)
                .createdAt(entity.getCreatedAt())
                .build();
    }
}
