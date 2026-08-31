package com.proyecto.proyectoSpringBoot.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class GuiaDespachoRequest {
    @NotBlank(message = "El número de guía es obligatorio")
    @Size(min = 2, max = 50, message = "El número de guía debe tener entre 2 y 50 caracteres")
    private String numeroGuia;

    @NotNull(message = "El ID del pedido es obligatorio")
    private Long pedidoId;

    @NotNull(message = "El ID de la transportadora es obligatorio")
    private Long transportadoraId;

    private LocalDate fechaEntregaEstimada;

    @Size(max = 150, message = "El nombre del conductor no puede exceder 150 caracteres")
    private String conductorNombre;

    @Size(max = 20, message = "La placa del vehículo no puede exceder 20 caracteres")
    private String placaVehiculo;

    private BigDecimal costoFlete;

    @Size(max = 500, message = "Las observaciones no pueden exceder 500 caracteres")
    private String observaciones;
}
