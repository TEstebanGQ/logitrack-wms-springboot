package com.proyecto.proyectoSpringBoot.controller;

import com.proyecto.proyectoSpringBoot.dto.request.PedidoClienteRequest;
import com.proyecto.proyectoSpringBoot.dto.response.PedidoClienteResponse;
import com.proyecto.proyectoSpringBoot.model.enums.EstadoPedido;
import com.proyecto.proyectoSpringBoot.service.interfaces.IPedidoClienteService;
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
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/pedidos")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Pedidos de Clientes", description = "Ciclo comercial de pedidos y despacho de ventas")
public class PedidoClienteController {

    private final IPedidoClienteService pedidoClienteService;

    @GetMapping
    @Operation(summary = "Listar todos los pedidos de clientes")
    public ResponseEntity<List<PedidoClienteResponse>> listarTodos() {
        return ResponseEntity.ok(pedidoClienteService.listarTodos());
    }

    @GetMapping("/cliente/{clienteId}")
    @Operation(summary = "Listar pedidos por cliente")
    public ResponseEntity<List<PedidoClienteResponse>> listarPorCliente(@PathVariable Long clienteId) {
        return ResponseEntity.ok(pedidoClienteService.listarPorCliente(clienteId));
    }

    @GetMapping("/estado/{estado}")
    @Operation(summary = "Listar pedidos por estado")
    public ResponseEntity<List<PedidoClienteResponse>> listarPorEstado(@PathVariable EstadoPedido estado) {
        return ResponseEntity.ok(pedidoClienteService.listarPorEstado(estado));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener pedido por ID con detalles")
    public ResponseEntity<PedidoClienteResponse> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(pedidoClienteService.obtenerPorId(id));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPERVISOR', 'EMPLEADO')")
    @Operation(summary = "Crear nuevo pedido de cliente (genera tareas de picking)")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Pedido creado exitosamente"),
        @ApiResponse(responseCode = "400", description = "Datos inválidos")
    })
    public ResponseEntity<PedidoClienteResponse> crear(
            @Valid @RequestBody PedidoClienteRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        String email = userDetails != null ? userDetails.getUsername() : "admin@logitrack.com";
        return ResponseEntity.status(HttpStatus.CREATED).body(pedidoClienteService.crear(request, email));
    }

    @PatchMapping("/{id}/estado")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPERVISOR')")
    @Operation(summary = "Cambiar estado de un pedido")
    public ResponseEntity<PedidoClienteResponse> cambiarEstado(
            @PathVariable Long id,
            @RequestParam EstadoPedido nuevoEstado,
            @RequestParam(required = false) String observaciones) {
        return ResponseEntity.ok(pedidoClienteService.cambiarEstado(id, nuevoEstado, observaciones));
    }

    @PostMapping("/{id}/despachar")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPERVISOR', 'EMPLEADO')")
    @Operation(summary = "Despachar pedido y generar movimiento automático de salida")
    public ResponseEntity<PedidoClienteResponse> despachar(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails) {
        String email = userDetails != null ? userDetails.getUsername() : "admin@logitrack.com";
        return ResponseEntity.ok(pedidoClienteService.despacharPedido(id, email));
    }

    @PostMapping("/{id}/cancelar")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPERVISOR')")
    @Operation(summary = "Cancelar pedido")
    public ResponseEntity<Void> cancelar(
            @PathVariable Long id,
            @RequestParam(required = false) String motivo) {
        pedidoClienteService.cancelar(id, motivo);
        return ResponseEntity.noContent().build();
    }
}
