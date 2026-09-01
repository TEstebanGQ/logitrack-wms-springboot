package com.proyecto.proyectoSpringBoot.dto.request;

import jakarta.validation.constraints.*;
import lombok.*;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CrearProductoRequest {
    @NotBlank(message = "El nombre del producto es obligatorio")
    @Size(min = 2, max = 150, message = "El nombre debe tener entre 2 y 150 caracteres")
    private String nombre;

    @NotNull(message = "La categoría es obligatoria")
    private Long categoriaId;

    private Long bodegaId;

    private java.util.Map<Long, Integer> stockPorBodega;

    @Builder.Default
    @Min(value = 0, message = "El stock inicial no puede ser negativo")
    private Integer stock = 0;

    @Builder.Default
    @Min(value = 1, message = "El stock mínimo debe ser al menos 1")
    private Integer stockMinimo = 10;

    @NotNull(message = "El precio es obligatorio")
    @DecimalMin(value = "0.0", message = "El precio no puede ser negativo")
    private BigDecimal precio;

    @Size(max = 500, message = "La descripción no puede exceder 500 caracteres")
    private String descripcion;
}
