package com.proyecto.proyectoSpringBoot.controller;

import com.proyecto.proyectoSpringBoot.dto.request.CrearClienteRequest;
import com.proyecto.proyectoSpringBoot.dto.response.ClienteResponse;
import com.proyecto.proyectoSpringBoot.dto.response.MovimientoResponse;
import com.proyecto.proyectoSpringBoot.service.interfaces.IClienteService;
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
@RequestMapping("/api/clientes")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Clientes", description = "Gestión de clientes y su historial de compras/despachos")
public class ClienteController {

    private final IClienteService clienteService;

    @GetMapping
    @Operation(summary = "Listar clientes (por defecto solo activos)")
    public ResponseEntity<List<ClienteResponse>> listar(
            @RequestParam(required = false, defaultValue = "true") boolean soloActivos) {
        return ResponseEntity.ok(clienteService.listar(soloActivos));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener cliente por ID")
    public ResponseEntity<ClienteResponse> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(clienteService.obtenerPorId(id));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPERVISOR', 'EMPLEADO', 'GERENTE_LOGISTICA')")
    @Operation(summary = "Registrar nuevo cliente")
    public ResponseEntity<ClienteResponse> crear(@Valid @RequestBody CrearClienteRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(clienteService.crear(request));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPERVISOR', 'GERENTE_LOGISTICA')")
    @Operation(summary = "Actualizar información de un cliente")
    public ResponseEntity<ClienteResponse> actualizar(
            @PathVariable Long id,
            @Valid @RequestBody CrearClienteRequest request) {
        return ResponseEntity.ok(clienteService.actualizar(id, request));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Eliminar (desactivar) cliente")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        clienteService.eliminar(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/movimientos")
    @Operation(summary = "Listar movimientos de despacho/salida asociados a este cliente")
    public ResponseEntity<List<MovimientoResponse>> listarMovimientos(@PathVariable Long id) {
        return ResponseEntity.ok(clienteService.listarMovimientosCliente(id));
    }
}
