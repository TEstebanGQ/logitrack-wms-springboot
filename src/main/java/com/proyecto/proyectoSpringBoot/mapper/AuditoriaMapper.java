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
                .recursoNombre(extractRecursoNombre(a))
                .tipoOperacion(a.getTipoOperacion())
                .fechaHora(a.getFechaHora())
                .usuarioEmail(a.getUsuario() != null ? a.getUsuario().getEmail() : "Sistema")
                .valoresAnteriores(a.getValoresAnteriores())
                .valoresNuevos(a.getValoresNuevos())
                .ipAddress(a.getIpAddress())
                .descripcion(a.getDescripcion())
                .build();
    }

    private String extractRecursoNombre(Auditoria a) {
        String json = a.getValoresNuevos() != null ? a.getValoresNuevos() : a.getValoresAnteriores();
        if (json != null) {
            if (json.contains("\"nombre\":\"")) {
                int start = json.indexOf("\"nombre\":\"") + 10;
                int end = json.indexOf("\"", start);
                if (end > start) return json.substring(start, end);
            }
            if (json.contains("\"bodegaNombre\":\"")) {
                int start = json.indexOf("\"bodegaNombre\":\"") + 16;
                int end = json.indexOf("\"", start);
                if (end > start) return json.substring(start, end);
            }
            if (json.contains("\"productoNombre\":\"")) {
                int start = json.indexOf("\"productoNombre\":\"") + 18;
                int end = json.indexOf("\"", start);
                if (end > start) return json.substring(start, end);
            }
        }
        if (a.getDescripcion() != null && a.getDescripcion().contains("'")) {
            int start = a.getDescripcion().indexOf("'") + 1;
            int end = a.getDescripcion().indexOf("'", start);
            if (end > start) return a.getDescripcion().substring(start, end);
        }
        return (a.getEntidad() != null ? a.getEntidad() : "Recurso") + " #" + (a.getEntidadId() != null ? a.getEntidadId() : "N/A");
    }
}
