package com.proyecto.proyectoSpringBoot.mapper;

import com.proyecto.proyectoSpringBoot.dto.response.ZonaBodegaResponse;
import com.proyecto.proyectoSpringBoot.model.entity.ZonaBodega;
import org.springframework.stereotype.Component;

@Component
public class ZonaBodegaMapper {

    public ZonaBodegaResponse toResponse(ZonaBodega z) {
        if (z == null) return null;
        return ZonaBodegaResponse.builder()
                .id(z.getId())
                .codigo(z.getCodigo())
                .nombre(z.getNombre())
                .tipoZona(z.getTipoZona())
                .bodegaId(z.getBodega() != null ? z.getBodega().getId() : null)
                .bodegaNombre(z.getBodega() != null ? z.getBodega().getNombre() : null)
                .temperaturaControlada(z.getTemperaturaControlada())
                .descripcion(z.getDescripcion())
                .activo(z.getActivo())
                .createdAt(z.getCreatedAt())
                .build();
    }
}
