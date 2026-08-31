package com.proyecto.proyectoSpringBoot.controller;

import com.proyecto.proyectoSpringBoot.dto.response.AlertaStockResponse;
import com.proyecto.proyectoSpringBoot.service.interfaces.IAlertaStockService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/alertas")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Alertas de Stock", description = "Gestión y resolución de alertas de stock mínimo")
public class AlertaStockController {

    private final IAlertaStockService alertaStockService;

    @GetMapping
    @Operation(summary = "Listar alertas de stock (todas o solo pendientes)")
    public ResponseEntity<List<AlertaStockResponse>> listar(
            @RequestParam(required = false, defaultValue = "false") boolean soloPendientes) {
        if (soloPendientes) {
            return ResponseEntity.ok(alertaStockService.listarPendientes());
        }
        return ResponseEntity.ok(alertaStockService.listarTodas());
    }

    @GetMapping("/pendientes")
    @Operation(summary = "Listar solo alertas pendientes")
    public ResponseEntity<List<AlertaStockResponse>> listarPendientes() {
        return ResponseEntity.ok(alertaStockService.listarPendientes());
    }

    @GetMapping("/producto/{productoId}")
    @Operation(summary = "Listar alertas por producto")
    public ResponseEntity<List<AlertaStockResponse>> listarPorProducto(@PathVariable Long productoId) {
        return ResponseEntity.ok(alertaStockService.listarPorProducto(productoId));
    }

    @PutMapping("/{id}/resolver")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Marcar una alerta como resuelta")
    public ResponseEntity<Void> resolver(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails) {
        alertaStockService.resolver(id, userDetails.getUsername());
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Eliminar o descartar una alerta de stock")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        alertaStockService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}

