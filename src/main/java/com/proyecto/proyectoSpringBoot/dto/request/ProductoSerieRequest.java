package com.proyecto.proyectoSpringBoot.dto.request;

import com.proyecto.proyectoSpringBoot.model.enums.EstadoSerie;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class ProductoSerieRequest {
    @NotBlank(message = "El número de serie es obligatorio")
    @Size(min = 2, max = 100, message = "El número de serie debe tener entre 2 y 100 caracteres")
    private String numeroSerie;

    @NotNull(message = "El ID del producto es obligatorio")
    private Long productoId;

    private Long bodegaId;
    private Long ubicacionId;
    private EstadoSerie estado;

    @Size(max = 255, message = "Las observaciones no pueden exceder 255 caracteres")
    private String observaciones;
}
