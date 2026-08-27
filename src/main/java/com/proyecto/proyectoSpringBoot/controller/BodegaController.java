package com.proyecto.proyectoSpringBoot.controller;

import com.proyecto.proyectoSpringBoot.dto.request.CrearBodegaRequest;
import com.proyecto.proyectoSpringBoot.dto.response.BodegaResponse;
import com.proyecto.proyectoSpringBoot.dto.response.InventarioBodegaResponse;
import com.proyecto.proyectoSpringBoot.service.interfaces.IBodegaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/bodegas")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Bodegas", description = "CRUD de bodegas e inventario por bodega")
public class BodegaController {

    private final IBodegaService bodegaService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Crear nueva bodega")
    public ResponseEntity<BodegaResponse> crear(@Valid @RequestBody CrearBodegaRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(bodegaService.crear(request));
    }

    @GetMapping
    @Operation(summary = "Listar bodegas (por defecto todas o solo activas)")
    public ResponseEntity<List<BodegaResponse>> listar(
            @RequestParam(required = false, defaultValue = "false") boolean soloActivas) {
        if (soloActivas) {
            return ResponseEntity.ok(bodegaService.listarActivas());
        }
        return ResponseEntity.ok(bodegaService.listarTodas());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener bodega por ID")
    public ResponseEntity<BodegaResponse> obtener(@PathVariable Long id) {
        return ResponseEntity.ok(bodegaService.obtenerPorId(id));
    }

    @GetMapping("/{id}/inventario")
    @Operation(summary = "Obtener inventario específico de productos en una bodega por ID")
    public ResponseEntity<List<InventarioBodegaResponse>> obtenerInventario(@PathVariable Long id) {
        return ResponseEntity.ok(bodegaService.obtenerInventarioPorBodega(id));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Actualizar bodega")
    public ResponseEntity<BodegaResponse> actualizar(@PathVariable Long id,
                                                      @Valid @RequestBody CrearBodegaRequest request) {
        return ResponseEntity.ok(bodegaService.actualizar(id, request));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Eliminar bodega (soft delete)")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        bodegaService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
