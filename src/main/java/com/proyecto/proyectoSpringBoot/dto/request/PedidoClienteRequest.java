package com.proyecto.proyectoSpringBoot.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDate;
import java.util.List;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class PedidoClienteRequest {
    @NotNull(message = "El ID del cliente es obligatorio")
    private Long clienteId;

    @NotNull(message = "El ID de la bodega de origen es obligatorio")
    private Long bodegaOrigenId;

    private LocalDate fechaCompromiso;
    private String direccionEntrega;
    private String observaciones;

    @NotEmpty(message = "El pedido debe contener al menos un detalle")
    @Valid
    private List<PedidoClienteDetalleRequest> detalles;
}
