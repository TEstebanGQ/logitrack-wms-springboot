package com.proyecto.proyectoSpringBoot.dto.response;

import lombok.*;

import java.time.LocalDateTime;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class TipoUbicacionResponse {
    private Long id;
    private String codigo;
    private String nombre;
    private Double pesoMaximoKg;
    private Double volumenMaximoM3;
    private String descripcion;
    private Boolean activo;
    private LocalDateTime createdAt;
}
