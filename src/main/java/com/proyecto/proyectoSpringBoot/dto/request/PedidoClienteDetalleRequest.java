package com.proyecto.proyectoSpringBoot.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.*;

import java.math.BigDecimal;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class PedidoClienteDetalleRequest {
    @NotNull(message = "El ID del producto es obligatorio")
    private Long productoId;

    @NotNull(message = "La cantidad solicitada es obligatoria")
    @Positive(message = "La cantidad solicitada debe ser positiva")
    private Integer cantidadSolicitada;

    private BigDecimal precioUnitario;
}
