package com.proyecto.proyectoSpringBoot.dto.response;

import com.proyecto.proyectoSpringBoot.model.enums.TipoMovimiento;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class MovimientoResponse {
    private Long id;
    private TipoMovimiento tipoMovimiento;
    private LocalDateTime fecha;
    private String observaciones;
    private String usuarioNombre;
    private String bodegaOrigen;
    private String bodegaDestino;
    private String proveedorNombre;
    private String clienteNombre;
    private List<DetalleResponse> detalles;

    @Data @Builder @NoArgsConstructor @AllArgsConstructor
    public static class DetalleResponse {
        private Long productoId;
        private String productoNombre;
        private Integer cantidad;
        private BigDecimal precioUnitario;
    }
}
