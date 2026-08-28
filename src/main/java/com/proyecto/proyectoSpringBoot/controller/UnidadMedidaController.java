package com.proyecto.proyectoSpringBoot.controller;

import com.proyecto.proyectoSpringBoot.dto.request.UnidadMedidaRequest;
import com.proyecto.proyectoSpringBoot.dto.response.UnidadMedidaResponse;
import com.proyecto.proyectoSpringBoot.service.interfaces.IUnidadMedidaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
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
@RequestMapping("/api/unidades-medida")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Unidades de Medida", description = "Gestión de unidades de empaque y conversión física")
public class UnidadMedidaController {

    private final IUnidadMedidaService unidadMedidaService;

    @GetMapping
    @Operation(summary = "Listar todas las unidades de medida")
    public ResponseEntity<List<UnidadMedidaResponse>> listarTodas() {
        return ResponseEntity.ok(unidadMedidaService.listarTodas());
    }

    @GetMapping("/activas")
    @Operation(summary = "Listar solo unidades de medida activas")
    public ResponseEntity<List<UnidadMedidaResponse>> listarActivas() {
        return ResponseEntity.ok(unidadMedidaService.listarActivas());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener unidad de medida por ID")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Unidad encontrada"),
        @ApiResponse(responseCode = "404", description = "Unidad no encontrada")
    })
    public ResponseEntity<UnidadMedidaResponse> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(unidadMedidaService.obtenerPorId(id));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPERVISOR')")
    @Operation(summary = "Crear nueva unidad de medida")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Unidad creada exitosamente"),
        @ApiResponse(responseCode = "400", description = "Datos inválidos")
    })
    public ResponseEntity<UnidadMedidaResponse> crear(@Valid @RequestBody UnidadMedidaRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(unidadMedidaService.crear(request));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPERVISOR')")
    @Operation(summary = "Actualizar unidad de medida")
    public ResponseEntity<UnidadMedidaResponse> actualizar(@PathVariable Long id, @Valid @RequestBody UnidadMedidaRequest request) {
        return ResponseEntity.ok(unidadMedidaService.actualizar(id, request));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Desactivar unidad de medida")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        unidadMedidaService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
