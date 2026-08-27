package com.proyecto.proyectoSpringBoot.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class ProductoResponse {
    private Long id;
    private String nombre;
    private String categoria;
    private Integer stock;
    private BigDecimal precio;
    private String descripcion;
    private boolean activo;
    private LocalDateTime createdAt;
}
