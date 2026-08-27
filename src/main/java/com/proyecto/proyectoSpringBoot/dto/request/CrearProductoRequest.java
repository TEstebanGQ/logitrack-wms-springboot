package com.proyecto.proyectoSpringBoot.dto.request;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class CrearProductoRequest {
    @NotBlank @Size(max = 150)
    private String nombre;
    @NotBlank @Size(max = 100)
    private String categoria;
    @Min(0)
    private Integer stock = 0;
    @NotNull @DecimalMin("0.0")
    private BigDecimal precio;
    @Size(max = 500)
    private String descripcion;
}
