package com.proyecto.proyectoSpringBoot.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class BodegaResponse {
    private Long id;
    private String nombre;
    private String ubicacion;
    private Integer capacidad;
    private String encargado;
    private boolean activo;
    private LocalDateTime createdAt;
}
