package com.proyecto.proyectoSpringBoot.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class TareaPickingRequest {
    @NotNull(message = "El ID del pedido es obligatorio")
    private Long pedidoId;

    @NotNull(message = "El ID del producto es obligatorio")
    private Long productoId;

    private Long ubicacionOrigenId;
    private Long usuarioAsignadoId;

    @NotNull(message = "La cantidad requerida es obligatoria")
    @Positive(message = "La cantidad requerida debe ser positiva")
    private Integer cantidadRequerida;

    private String notas;
}
