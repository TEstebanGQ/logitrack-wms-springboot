package com.proyecto.proyectoSpringBoot.dto.response;

import com.proyecto.proyectoSpringBoot.model.enums.TipoZona;
import lombok.*;

import java.time.LocalDateTime;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class ZonaBodegaResponse {
    private Long id;
    private String codigo;
    private String nombre;
    private TipoZona tipoZona;
    private Long bodegaId;
    private String bodegaNombre;
    private Boolean temperaturaControlada;
    private String descripcion;
    private Boolean activo;
    private LocalDateTime createdAt;
}
