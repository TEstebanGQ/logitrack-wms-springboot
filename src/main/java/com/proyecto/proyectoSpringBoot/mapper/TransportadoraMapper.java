package com.proyecto.proyectoSpringBoot.mapper;

import com.proyecto.proyectoSpringBoot.dto.response.TransportadoraResponse;
import com.proyecto.proyectoSpringBoot.model.entity.Transportadora;
import org.springframework.stereotype.Component;

@Component
public class TransportadoraMapper {

    public TransportadoraResponse toResponse(Transportadora t) {
        if (t == null) return null;
        return TransportadoraResponse.builder()
                .id(t.getId())
                .nombre(t.getNombre())
                .rucNit(t.getRucNit())
                .telefono(t.getTelefono())
                .email(t.getEmail())
                .contacto(t.getContacto())
                .tipoServicio(t.getTipoServicio())
                .activo(t.getActivo())
                .createdAt(t.getCreatedAt())
                .build();
    }
}
