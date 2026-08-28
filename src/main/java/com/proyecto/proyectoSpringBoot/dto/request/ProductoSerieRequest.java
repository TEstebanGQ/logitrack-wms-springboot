package com.proyecto.proyectoSpringBoot.dto.request;

import com.proyecto.proyectoSpringBoot.model.enums.EstadoSerie;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class ProductoSerieRequest {
    @NotBlank(message = "El número de serie es obligatorio")
    private String numeroSerie;

    @NotNull(message = "El ID del producto es obligatorio")
    private Long productoId;

    private Long bodegaId;
    private Long ubicacionId;
    private EstadoSerie estado;
    private String observaciones;
}
