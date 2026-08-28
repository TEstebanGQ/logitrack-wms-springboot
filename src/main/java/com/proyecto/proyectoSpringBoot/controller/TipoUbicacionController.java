package com.proyecto.proyectoSpringBoot.controller;

import com.proyecto.proyectoSpringBoot.dto.request.TipoUbicacionRequest;
import com.proyecto.proyectoSpringBoot.dto.response.TipoUbicacionResponse;
import com.proyecto.proyectoSpringBoot.service.interfaces.ITipoUbicacionService;
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
@RequestMapping("/api/tipos-ubicacion")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Tipos de Ubicación", description = "Definición de tipologías y capacidades de almacenamiento")
public class TipoUbicacionController {

    private final ITipoUbicacionService tipoUbicacionService;

    @GetMapping
    @Operation(summary = "Listar todos los tipos de ubicación")
    public ResponseEntity<List<TipoUbicacionResponse>> listarTodas() {
        return ResponseEntity.ok(tipoUbicacionService.listarTodas());
    }

    @GetMapping("/activas")
    @Operation(summary = "Listar tipos de ubicación activos")
    public ResponseEntity<List<TipoUbicacionResponse>> listarActivas() {
        return ResponseEntity.ok(tipoUbicacionService.listarActivas());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener tipo de ubicación por ID")
    public ResponseEntity<TipoUbicacionResponse> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(tipoUbicacionService.obtenerPorId(id));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPERVISOR')")
    @Operation(summary = "Crear nuevo tipo de ubicación")
    public ResponseEntity<TipoUbicacionResponse> crear(@Valid @RequestBody TipoUbicacionRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(tipoUbicacionService.crear(request));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPERVISOR')")
    @Operation(summary = "Actualizar tipo de ubicación")
    public ResponseEntity<TipoUbicacionResponse> actualizar(@PathVariable Long id, @Valid @RequestBody TipoUbicacionRequest request) {
        return ResponseEntity.ok(tipoUbicacionService.actualizar(id, request));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Desactivar tipo de ubicación")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        tipoUbicacionService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
