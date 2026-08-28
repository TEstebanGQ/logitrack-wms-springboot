package com.proyecto.proyectoSpringBoot.dto.response;

import lombok.*;

import java.math.BigDecimal;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class PedidoClienteDetalleResponse {
    private Long id;
    private Long productoId;
    private String productoNombre;
    private Integer cantidadSolicitada;
    private Integer cantidadDespachada;
    private BigDecimal precioUnitario;
    private BigDecimal subtotal;
}
