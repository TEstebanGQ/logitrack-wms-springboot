package com.proyecto.proyectoSpringBoot.dto.response;

import com.proyecto.proyectoSpringBoot.model.enums.EstadoPedido;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class PedidoClienteResponse {
    private Long id;
    private String codigoPedido;
    private Long clienteId;
    private String clienteNombre;
    private Long bodegaOrigenId;
    private String bodegaOrigenNombre;
    private EstadoPedido estado;
    private BigDecimal totalPedido;
    private LocalDateTime fechaPedido;
    private LocalDate fechaCompromiso;
    private String direccionEntrega;
    private String observaciones;
    private String usuarioCreadorNombre;
    private List<PedidoClienteDetalleResponse> detalles;
}
