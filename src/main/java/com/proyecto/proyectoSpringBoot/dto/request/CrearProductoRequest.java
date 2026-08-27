package com.proyecto.proyectoSpringBoot.dto.request;

import jakarta.validation.constraints.*;
import lombok.*;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CrearProductoRequest {
    @NotBlank @Size(max = 150)
    private String nombre;
    @NotNull
    private Long categoriaId;
    @Builder.Default
    @Min(0)
    private Integer stock = 0;
    @NotNull @DecimalMin("0.0")
    private BigDecimal precio;
    @Size(max = 500)
    private String descripcion;
}
