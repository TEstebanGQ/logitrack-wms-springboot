package com.proyecto.proyectoSpringBoot.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReporteDiarioDTO {
    private LocalDateTime fechaGeneracion;
    private long totalMovimientosHoy;
    private long totalEntradas;
    private long totalSalidas;
    private long totalTransferencias;
    private long totalAlertasStockBajo;
    private long totalConteosRealizados;
    private List<MovimientoResumen> movimientos;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MovimientoResumen {
        private String fechaHora;
        private String usuarioNombre;
        private String usuarioEmail;
        private String tipoMovimiento;
        private String productoNombre;
        private Integer cantidad;
        private String bodegaNombre;
        private String observaciones;
    }
}
