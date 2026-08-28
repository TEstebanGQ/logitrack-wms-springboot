package com.proyecto.proyectoSpringBoot.dto.response;

import com.proyecto.proyectoSpringBoot.model.enums.EstadoEnvio;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class GuiaDespachoResponse {
    private Long id;
    private String numeroGuia;
    private Long pedidoId;
    private String codigoPedido;
    private String clienteNombre;
    private Long transportadoraId;
    private String transportadoraNombre;
    private LocalDateTime fechaDespacho;
    private LocalDate fechaEntregaEstimada;
    private LocalDateTime fechaEntregaReal;
    private EstadoEnvio estadoEnvio;
    private String conductorNombre;
    private String placaVehiculo;
    private BigDecimal costoFlete;
    private String observaciones;
}
