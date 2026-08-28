package com.proyecto.proyectoSpringBoot.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class GuiaDespachoRequest {
    @NotBlank(message = "El número de guía es obligatorio")
    private String numeroGuia;

    @NotNull(message = "El ID del pedido es obligatorio")
    private Long pedidoId;

    @NotNull(message = "El ID de la transportadora es obligatorio")
    private Long transportadoraId;

    private LocalDate fechaEntregaEstimada;
    private String conductorNombre;
    private String placaVehiculo;
    private BigDecimal costoFlete;
    private String observaciones;
}
