package com.proyecto.proyectoSpringBoot.controller;

import com.proyecto.proyectoSpringBoot.dto.request.ZonaBodegaRequest;
import com.proyecto.proyectoSpringBoot.dto.response.ZonaBodegaResponse;
import com.proyecto.proyectoSpringBoot.model.enums.TipoZona;
import com.proyecto.proyectoSpringBoot.service.interfaces.IZonaBodegaService;
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
@RequestMapping("/api/zonas")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Zonas de Bodega", description = "Gestión de macro-zonas operativas de almacenamiento")
public class ZonaBodegaController {

    private final IZonaBodegaService zonaBodegaService;

    @GetMapping
    @Operation(summary = "Listar todas las zonas de bodega")
    public ResponseEntity<List<ZonaBodegaResponse>> listarTodas() {
        return ResponseEntity.ok(zonaBodegaService.listarTodas());
    }

    @GetMapping("/bodega/{bodegaId}")
    @Operation(summary = "Listar zonas por bodega")
    public ResponseEntity<List<ZonaBodegaResponse>> listarPorBodega(@PathVariable Long bodegaId) {
        return ResponseEntity.ok(zonaBodegaService.listarPorBodega(bodegaId));
    }

    @GetMapping("/tipo/{tipoZona}")
    @Operation(summary = "Listar zonas por tipo de zona")
    public ResponseEntity<List<ZonaBodegaResponse>> listarPorTipo(@PathVariable TipoZona tipoZona) {
        return ResponseEntity.ok(zonaBodegaService.listarPorTipo(tipoZona));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener zona por ID")
    public ResponseEntity<ZonaBodegaResponse> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(zonaBodegaService.obtenerPorId(id));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPERVISOR')")
    @Operation(summary = "Crear nueva zona de bodega")
    public ResponseEntity<ZonaBodegaResponse> crear(@Valid @RequestBody ZonaBodegaRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(zonaBodegaService.crear(request));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPERVISOR')")
    @Operation(summary = "Actualizar zona de bodega")
    public ResponseEntity<ZonaBodegaResponse> actualizar(@PathVariable Long id, @Valid @RequestBody ZonaBodegaRequest request) {
        return ResponseEntity.ok(zonaBodegaService.actualizar(id, request));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Desactivar zona de bodega")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        zonaBodegaService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
