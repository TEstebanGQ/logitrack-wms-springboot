package com.proyecto.proyectoSpringBoot.dto.response;

import lombok.*;

import java.time.LocalDateTime;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class UnidadMedidaResponse {
    private Long id;
    private String codigo;
    private String nombre;
    private String abreviatura;
    private Double factorConversion;
    private Boolean activo;
    private LocalDateTime createdAt;
}
