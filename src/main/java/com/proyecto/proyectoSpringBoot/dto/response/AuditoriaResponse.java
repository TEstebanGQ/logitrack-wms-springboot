package com.proyecto.proyectoSpringBoot.dto.response;

import com.proyecto.proyectoSpringBoot.model.enums.TipoOperacion;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class AuditoriaResponse {
    private Long id;
    private String entidad;
    private Long entidadId;
    private String recursoNombre;
    private TipoOperacion tipoOperacion;
    private LocalDateTime fechaHora;
    private String usuarioEmail;
    private String valoresAnteriores;
    private String valoresNuevos;
    private String ipAddress;
    private String descripcion;
}
