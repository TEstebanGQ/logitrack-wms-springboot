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
