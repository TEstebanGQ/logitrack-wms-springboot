package com.proyecto.proyectoSpringBoot.dto.response;

import com.proyecto.proyectoSpringBoot.model.enums.EstadoOrdenCompra;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrdenCompraResponse {
    private Long id;
    private String codigoOrden;
    private Long proveedorId;
    private String proveedorNombre;
    private Long bodegaDestinoId;
    private String bodegaDestinoNombre;
    private EstadoOrdenCompra estado;
    private BigDecimal totalEstimado;
    private LocalDateTime fechaSolicitud;
    private LocalDate fechaEntregaEsperada;
    private String observaciones;
    private String usuarioSolicitante;
    private List<DetalleOrdenResponse> detalles;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DetalleOrdenResponse {
        private Long id;
        private Long productoId;
        private String productoNombre;
        private Integer cantidad;
        private BigDecimal precioUnitario;
        private BigDecimal subtotal;
    }
}
