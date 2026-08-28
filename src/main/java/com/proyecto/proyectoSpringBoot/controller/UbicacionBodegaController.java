package com.proyecto.proyectoSpringBoot.controller;

import com.proyecto.proyectoSpringBoot.dto.request.CrearUbicacionRequest;
import com.proyecto.proyectoSpringBoot.dto.response.UbicacionBodegaResponse;
import com.proyecto.proyectoSpringBoot.service.interfaces.IUbicacionBodegaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/ubicaciones")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Ubicaciones Físicas de Bodega", description = "Gestión de pasillos, estantes, racks y posiciones internas por bodega")
public class UbicacionBodegaController {

    private final IUbicacionBodegaService ubicacionService;

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN','SUPERVISOR')")
    @Operation(summary = "Registrar una nueva ubicación física dentro de una bodega")
    public ResponseEntity<UbicacionBodegaResponse> crear(
            @Valid @RequestBody CrearUbicacionRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        UbicacionBodegaResponse response = ubicacionService.crearUbicacion(request, userDetails.getUsername());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Listar todas las ubicaciones físicas")
    public ResponseEntity<List<UbicacionBodegaResponse>> listar() {
        return ResponseEntity.ok(ubicacionService.listarTodas());
    }

    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Obtener detalle de una ubicación por ID")
    public ResponseEntity<UbicacionBodegaResponse> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(ubicacionService.obtenerPorId(id));
    }

    @GetMapping("/bodega/{bodegaId}")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Listar ubicaciones de una bodega específica")
    public ResponseEntity<List<UbicacionBodegaResponse>> listarPorBodega(
            @PathVariable Long bodegaId,
            @RequestParam(defaultValue = "false") boolean soloActivos) {
        return ResponseEntity.ok(ubicacionService.listarPorBodega(bodegaId, soloActivos));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Desactivar una ubicación física de bodega")
    public ResponseEntity<Void> eliminar(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails) {
        ubicacionService.desactivarUbicacion(id, userDetails.getUsername());
        return ResponseEntity.noContent().build();
    }
}
