package com.proyecto.proyectoSpringBoot.mapper;

import com.proyecto.proyectoSpringBoot.dto.response.AuditoriaResponse;
import com.proyecto.proyectoSpringBoot.model.entity.Auditoria;
import org.springframework.stereotype.Component;

@Component
public class AuditoriaMapper {

    public AuditoriaResponse toResponse(Auditoria a) {
        return AuditoriaResponse.builder()
                .id(a.getId())
                .entidad(a.getEntidad())
                .entidadId(a.getEntidadId())
                .tipoOperacion(a.getTipoOperacion())
                .fechaHora(a.getFechaHora())
                .usuarioEmail(a.getUsuario() != null ? a.getUsuario().getEmail() : "Sistema")
                .valoresAnteriores(a.getValoresAnteriores())
                .valoresNuevos(a.getValoresNuevos())
                .ipAddress(a.getIpAddress())
                .descripcion(a.getDescripcion())
                .build();
    }
}
