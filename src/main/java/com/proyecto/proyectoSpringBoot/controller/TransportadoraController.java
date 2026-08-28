package com.proyecto.proyectoSpringBoot.controller;

import com.proyecto.proyectoSpringBoot.dto.request.TransportadoraRequest;
import com.proyecto.proyectoSpringBoot.dto.response.TransportadoraResponse;
import com.proyecto.proyectoSpringBoot.service.interfaces.ITransportadoraService;
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
@RequestMapping("/api/transportadoras")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Transportadoras", description = "Gestión de empresas de transporte y convenios logísticos")
public class TransportadoraController {

    private final ITransportadoraService transportadoraService;

    @GetMapping
    @Operation(summary = "Listar todas las transportadoras")
    public ResponseEntity<List<TransportadoraResponse>> listarTodas() {
        return ResponseEntity.ok(transportadoraService.listarTodas());
    }

    @GetMapping("/activas")
    @Operation(summary = "Listar transportadoras activas")
    public ResponseEntity<List<TransportadoraResponse>> listarActivas() {
        return ResponseEntity.ok(transportadoraService.listarActivas());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener transportadora por ID")
    public ResponseEntity<TransportadoraResponse> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(transportadoraService.obtenerPorId(id));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPERVISOR')")
    @Operation(summary = "Crear nueva transportadora")
    public ResponseEntity<TransportadoraResponse> crear(@Valid @RequestBody TransportadoraRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(transportadoraService.crear(request));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPERVISOR')")
    @Operation(summary = "Actualizar transportadora")
    public ResponseEntity<TransportadoraResponse> actualizar(@PathVariable Long id, @Valid @RequestBody TransportadoraRequest request) {
        return ResponseEntity.ok(transportadoraService.actualizar(id, request));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Desactivar transportadora")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        transportadoraService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
