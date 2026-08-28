package com.proyecto.proyectoSpringBoot.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class ConteoCiclicoDetalleRequest {
    @NotNull(message = "El ID del producto es obligatorio")
    private Long productoId;

    private Long ubicacionId;
    private Integer stockFisico;
    private String notas;
}
