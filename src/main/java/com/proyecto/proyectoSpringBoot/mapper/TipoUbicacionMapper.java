package com.proyecto.proyectoSpringBoot.mapper;

import com.proyecto.proyectoSpringBoot.dto.response.TipoUbicacionResponse;
import com.proyecto.proyectoSpringBoot.model.entity.TipoUbicacion;
import org.springframework.stereotype.Component;

@Component
public class TipoUbicacionMapper {

    public TipoUbicacionResponse toResponse(TipoUbicacion t) {
        if (t == null) return null;
        return TipoUbicacionResponse.builder()
                .id(t.getId())
                .codigo(t.getCodigo())
                .nombre(t.getNombre())
                .pesoMaximoKg(t.getPesoMaximoKg())
                .volumenMaximoM3(t.getVolumenMaximoM3())
                .descripcion(t.getDescripcion())
                .activo(t.getActivo())
                .createdAt(t.getCreatedAt())
                .build();
    }
}
