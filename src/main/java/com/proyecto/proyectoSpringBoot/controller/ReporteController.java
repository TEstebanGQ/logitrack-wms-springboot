package com.proyecto.proyectoSpringBoot.controller;

import com.proyecto.proyectoSpringBoot.dto.response.ReporteStockResponse;
import com.proyecto.proyectoSpringBoot.service.interfaces.IReporteService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/reportes")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Reportes", description = "Reportes y estadísticas del sistema")
public class ReporteController {

    private final IReporteService reporteService;

    @GetMapping("/general")
    @Operation(summary = "Reporte general: stock por bodega y productos más movidos")
    public ResponseEntity<ReporteStockResponse> reporteGeneral() {
        return ResponseEntity.ok(reporteService.generarReporteGeneral());
    }
}
