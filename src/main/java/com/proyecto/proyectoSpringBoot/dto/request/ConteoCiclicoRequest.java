package com.proyecto.proyectoSpringBoot.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDate;
import java.util.List;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class ConteoCiclicoRequest {
    @NotNull(message = "El ID de la bodega es obligatorio")
    private Long bodegaId;

    private Long zonaId;
    private LocalDate fechaProgramada;
    private String observaciones;
    private List<ConteoCiclicoDetalleRequest> detalles;
}
