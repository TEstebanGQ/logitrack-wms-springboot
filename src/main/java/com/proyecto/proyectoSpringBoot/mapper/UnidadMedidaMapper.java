package com.proyecto.proyectoSpringBoot.mapper;

import com.proyecto.proyectoSpringBoot.dto.response.UnidadMedidaResponse;
import com.proyecto.proyectoSpringBoot.model.entity.UnidadMedida;
import org.springframework.stereotype.Component;

@Component
public class UnidadMedidaMapper {

    public UnidadMedidaResponse toResponse(UnidadMedida u) {
        if (u == null) return null;
        return UnidadMedidaResponse.builder()
                .id(u.getId())
                .codigo(u.getCodigo())
                .nombre(u.getNombre())
                .abreviatura(u.getAbreviatura())
                .factorConversion(u.getFactorConversion())
                .activo(u.getActivo())
                .createdAt(u.getCreatedAt())
                .build();
    }
}
