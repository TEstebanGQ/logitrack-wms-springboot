package com.proyecto.proyectoSpringBoot.controller;

import com.proyecto.proyectoSpringBoot.dto.response.ReporteStockResponse;
import com.proyecto.proyectoSpringBoot.service.interfaces.IReporteExportService;
import com.proyecto.proyectoSpringBoot.service.interfaces.IReporteService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/reportes")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('ADMIN', 'SUPERVISOR', 'GERENTE_LOGISTICA')")
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Reportes", description = "Reportes y estadísticas del sistema")
public class ReporteController {

    private final IReporteService reporteService;
    private final IReporteExportService reporteExportService;

    @GetMapping({"/general", "/resumen"})
    @Operation(summary = "Reporte general: stock por bodega y productos más movidos")
    public ResponseEntity<ReporteStockResponse> reporteGeneral() {
        return ResponseEntity.ok(reporteService.generarReporteGeneral());
    }

    @GetMapping("/movimientos")
    @Operation(summary = "Reporte de movimientos de inventario con filtros avanzados",
               description = "Devuelve movimientos filtrados por bodega, producto, tipo y rango de fechas")
    public ResponseEntity<List<com.proyecto.proyectoSpringBoot.dto.response.MovimientoResponse>> reporteMovimientos(
            @RequestParam(required = false) Long bodega,
            @RequestParam(required = false) Long producto,
            @RequestParam(required = false) com.proyecto.proyectoSpringBoot.model.enums.TipoMovimiento tipoMovimiento,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaInicio,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaFin) {

        java.time.LocalDateTime inicio = fechaInicio != null ? fechaInicio.atStartOfDay() : null;
        java.time.LocalDateTime fin = fechaFin != null ? fechaFin.atTime(23, 59, 59) : null;

        return ResponseEntity.ok(reporteService.consultarMovimientosFiltrados(bodega, producto, tipoMovimiento, inicio, fin));
    }

    @GetMapping("/auditoria")
    @Operation(summary = "Reporte de auditoría con filtros avanzados",
               description = "Devuelve registros de auditoría filtrados por producto, rango de fechas y campo modificado")
    public ResponseEntity<List<com.proyecto.proyectoSpringBoot.dto.response.AuditoriaResponse>> reporteAuditoria(
            @RequestParam(required = false) Long producto,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaInicio,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaFin,
            @RequestParam(required = false) String campoModificado) {

        java.time.LocalDateTime inicio = fechaInicio != null ? fechaInicio.atStartOfDay() : null;
        java.time.LocalDateTime fin = fechaFin != null ? fechaFin.atTime(23, 59, 59) : null;

        return ResponseEntity.ok(reporteService.consultarAuditoriaFiltrada(producto, inicio, fin, campoModificado));
    }

    @GetMapping("/clasificacion-abc")
    @Operation(summary = "Clasificación ABC de inventario según valorización (80-15-5)")
    public ResponseEntity<com.proyecto.proyectoSpringBoot.dto.response.ClasificacionAbcResponse> clasificacionAbc() {
        return ResponseEntity.ok(reporteService.calcularClasificacionABC());
    }

    @GetMapping("/exportar/excel")
    @Operation(summary = "Exportar resumen general a Excel")
    public ResponseEntity<byte[]> exportarExcel() {
        byte[] data = reporteExportService.exportarResumenExcel();
        return ResponseEntity.ok()
            .header("Content-Disposition", "attachment; filename=logitrack-resumen.xlsx")
            .header("Content-Type", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")
            .body(data);
    }

    @GetMapping("/exportar/pdf")
    @Operation(summary = "Exportar resumen general a PDF")
    public ResponseEntity<byte[]> exportarPdf() {
        byte[] data = reporteExportService.exportarResumenPdf();
        return ResponseEntity.ok()
            .header("Content-Disposition", "attachment; filename=logitrack-resumen.pdf")
            .header("Content-Type", "application/pdf")
            .body(data);
    }

    @GetMapping("/movimientos/exportar/excel")
    @Operation(summary = "Exportar movimientos por rango a Excel")
    public ResponseEntity<byte[]> exportarMovimientosExcel(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate desde,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate hasta) {
        byte[] data = reporteExportService.exportarMovimientosExcel(desde, hasta);
        return ResponseEntity.ok()
            .header("Content-Disposition", "attachment; filename=logitrack-movimientos.xlsx")
            .header("Content-Type", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")
            .body(data);
    }

    @GetMapping("/movimientos/exportar/pdf")
    @Operation(summary = "Exportar movimientos por rango a PDF")
    public ResponseEntity<byte[]> exportarMovimientosPdf(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate desde,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate hasta) {
        byte[] data = reporteExportService.exportarMovimientosPdf(desde, hasta);
        return ResponseEntity.ok()
            .header("Content-Disposition", "attachment; filename=logitrack-movimientos.pdf")
            .header("Content-Type", "application/pdf")
            .body(data);
    }

    @GetMapping("/auditorias/exportar/excel")
    @Operation(summary = "Exportar bitácora de auditoría a Excel")
    public ResponseEntity<byte[]> exportarAuditoriasExcel() {
        byte[] data = reporteExportService.exportarAuditoriasExcel();
        return ResponseEntity.ok()
            .header("Content-Disposition", "attachment; filename=logitrack-auditoria.xlsx")
            .header("Content-Type", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")
            .body(data);
    }

    @GetMapping("/auditorias/exportar/pdf")
    @Operation(summary = "Exportar bitácora de auditoría a PDF")
    public ResponseEntity<byte[]> exportarAuditoriasPdf() {
        byte[] data = reporteExportService.exportarAuditoriasPdf();
        return ResponseEntity.ok()
            .header("Content-Disposition", "attachment; filename=logitrack-auditoria.pdf")
            .header("Content-Type", "application/pdf")
            .body(data);
    }
}
