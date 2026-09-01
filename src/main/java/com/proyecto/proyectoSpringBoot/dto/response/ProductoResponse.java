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
    private Long categoriaId;
    private String categoriaNombre;
    private Long bodegaId;
    private String bodegaNombre;
    private Integer stock;
    private Integer stockMinimo;
    private BigDecimal precio;
    private String descripcion;
    private boolean activo;
    private LocalDateTime createdAt;
}
