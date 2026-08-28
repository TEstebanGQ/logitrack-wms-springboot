package com.proyecto.proyectoSpringBoot.controller;

import com.proyecto.proyectoSpringBoot.dto.request.CrearProveedorRequest;
import com.proyecto.proyectoSpringBoot.dto.response.ProveedorResponse;
import com.proyecto.proyectoSpringBoot.dto.response.MovimientoResponse;
import com.proyecto.proyectoSpringBoot.service.interfaces.IProveedorService;
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
@RequestMapping("/api/proveedores")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Proveedores", description = "Gestión de proveedores y su historial de suministros/entradas")
public class ProveedorController {

    private final IProveedorService proveedorService;

    @GetMapping
    @Operation(summary = "Listar proveedores (por defecto solo activos)")
    public ResponseEntity<List<ProveedorResponse>> listar(
            @RequestParam(required = false, defaultValue = "true") boolean soloActivos) {
        return ResponseEntity.ok(proveedorService.listar(soloActivos));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener proveedor por ID")
    public ResponseEntity<ProveedorResponse> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(proveedorService.obtenerPorId(id));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPERVISOR', 'JEFE_COMPRAS', 'GERENTE_LOGISTICA')")
    @Operation(summary = "Registrar nuevo proveedor")
    public ResponseEntity<ProveedorResponse> crear(@Valid @RequestBody CrearProveedorRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(proveedorService.crear(request));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPERVISOR', 'JEFE_COMPRAS', 'GERENTE_LOGISTICA')")
    @Operation(summary = "Actualizar información de un proveedor")
    public ResponseEntity<ProveedorResponse> actualizar(
            @PathVariable Long id,
            @Valid @RequestBody CrearProveedorRequest request) {
        return ResponseEntity.ok(proveedorService.actualizar(id, request));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Eliminar (desactivar) proveedor")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        proveedorService.eliminar(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/movimientos")
    @Operation(summary = "Listar movimientos de entrada/suministro asociados a este proveedor")
    public ResponseEntity<List<MovimientoResponse>> listarMovimientos(@PathVariable Long id) {
        return ResponseEntity.ok(proveedorService.listarMovimientosProveedor(id));
    }
}
