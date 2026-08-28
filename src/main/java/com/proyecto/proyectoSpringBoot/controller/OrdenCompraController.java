package com.proyecto.proyectoSpringBoot.controller;

import com.proyecto.proyectoSpringBoot.dto.request.CrearOrdenCompraRequest;
import com.proyecto.proyectoSpringBoot.dto.response.OrdenCompraResponse;
import com.proyecto.proyectoSpringBoot.model.enums.EstadoOrdenCompra;
import com.proyecto.proyectoSpringBoot.service.interfaces.IOrdenCompraService;
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
import java.util.Map;

@RestController
@RequestMapping("/api/ordenes-compra")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Órdenes de Compra", description = "Ciclo completo de pedidos a proveedores y recepción automática en almacenes")
public class OrdenCompraController {

    private final IOrdenCompraService ordenCompraService;

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN','SUPERVISOR','JEFE_COMPRAS')")
    @Operation(summary = "Crear una nueva orden de compra a un proveedor", description = "Registra solicitud de reabastecimiento en estado PENDIENTE")
    @io.swagger.v3.oas.annotations.responses.ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "Orden de compra generada exitosamente"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Parámetros inválidos o proveedor inactivo"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Proveedor o bodega no encontrada")
    })
    public ResponseEntity<OrdenCompraResponse> crear(
            @Valid @RequestBody CrearOrdenCompraRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        OrdenCompraResponse response = ordenCompraService.crearOrden(request, userDetails.getUsername());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Listar todas las órdenes de compra")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Lista obtenida exitosamente")
    public ResponseEntity<List<OrdenCompraResponse>> listar() {
        return ResponseEntity.ok(ordenCompraService.listarTodas());
    }

    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Obtener el detalle de una orden de compra por ID")
    public ResponseEntity<OrdenCompraResponse> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(ordenCompraService.obtenerPorId(id));
    }

    @GetMapping("/proveedor/{proveedorId}")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Listar órdenes de compra por proveedor")
    public ResponseEntity<List<OrdenCompraResponse>> listarPorProveedor(@PathVariable Long proveedorId) {
        return ResponseEntity.ok(ordenCompraService.listarPorProveedor(proveedorId));
    }

    @GetMapping("/estado/{estado}")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Listar órdenes de compra por estado (PENDIENTE, APROBADA, RECIBIDA, CANCELADA)")
    public ResponseEntity<List<OrdenCompraResponse>> listarPorEstado(@PathVariable EstadoOrdenCompra estado) {
        return ResponseEntity.ok(ordenCompraService.listarPorEstado(estado));
    }

    @PutMapping("/{id}/aprobar")
    @PreAuthorize("hasAnyRole('ADMIN','SUPERVISOR','JEFE_COMPRAS')")
    @Operation(summary = "Aprobar una orden de compra pendiente")
    public ResponseEntity<OrdenCompraResponse> aprobar(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(ordenCompraService.aprobarOrden(id, userDetails.getUsername()));
    }

    @PutMapping("/{id}/cancelar")
    @PreAuthorize("hasAnyRole('ADMIN','SUPERVISOR','JEFE_COMPRAS')")
    @Operation(summary = "Cancelar una orden de compra")
    public ResponseEntity<OrdenCompraResponse> cancelar(
            @PathVariable Long id,
            @RequestBody(required = false) Map<String, String> body,
            @AuthenticationPrincipal UserDetails userDetails) {
        String motivo = body != null ? body.get("motivo") : null;
        return ResponseEntity.ok(ordenCompraService.cancelarOrden(id, motivo, userDetails.getUsername()));
    }

    @PutMapping("/{id}/recibir")
    @PreAuthorize("hasAnyRole('ADMIN','SUPERVISOR','EMPLEADO','JEFE_COMPRAS')")
    @Operation(summary = "Recibir la mercancía de la orden de compra e ingresar automáticamente al inventario")
    public ResponseEntity<OrdenCompraResponse> recibir(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(ordenCompraService.recibirOrden(id, userDetails.getUsername()));
    }
}
