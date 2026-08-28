package com.proyecto.proyectoSpringBoot.controller;

import com.proyecto.proyectoSpringBoot.dto.request.GuiaDespachoRequest;
import com.proyecto.proyectoSpringBoot.dto.response.GuiaDespachoResponse;
import com.proyecto.proyectoSpringBoot.model.enums.EstadoEnvio;
import com.proyecto.proyectoSpringBoot.service.interfaces.IGuiaDespachoService;
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
@RequestMapping("/api/guias-despacho")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Guías de Despacho", description = "Albaranes de transporte, guías de despacho y tracking de envíos")
public class GuiaDespachoController {

    private final IGuiaDespachoService guiaDespachoService;

    @GetMapping
    @Operation(summary = "Listar todas las guías de despacho")
    public ResponseEntity<List<GuiaDespachoResponse>> listarTodas() {
        return ResponseEntity.ok(guiaDespachoService.listarTodas());
    }

    @GetMapping("/transportadora/{transportadoraId}")
    @Operation(summary = "Listar guías por transportadora")
    public ResponseEntity<List<GuiaDespachoResponse>> listarPorTransportadora(@PathVariable Long transportadoraId) {
        return ResponseEntity.ok(guiaDespachoService.listarPorTransportadora(transportadoraId));
    }

    @GetMapping("/estado/{estado}")
    @Operation(summary = "Listar guías por estado de envío")
    public ResponseEntity<List<GuiaDespachoResponse>> listarPorEstado(@PathVariable EstadoEnvio estado) {
        return ResponseEntity.ok(guiaDespachoService.listarPorEstado(estado));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener guía por ID")
    public ResponseEntity<GuiaDespachoResponse> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(guiaDespachoService.obtenerPorId(id));
    }

    @GetMapping("/pedido/{pedidoId}")
    @Operation(summary = "Obtener guía por ID de pedido")
    public ResponseEntity<GuiaDespachoResponse> obtenerPorPedido(@PathVariable Long pedidoId) {
        return ResponseEntity.ok(guiaDespachoService.obtenerPorPedido(pedidoId));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPERVISOR', 'EMPLEADO')")
    @Operation(summary = "Generar nueva guía de despacho")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Guía generada exitosamente"),
        @ApiResponse(responseCode = "400", description = "Datos inválidos")
    })
    public ResponseEntity<GuiaDespachoResponse> generarGuia(@Valid @RequestBody GuiaDespachoRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(guiaDespachoService.generarGuia(request));
    }

    @PatchMapping("/{id}/estado")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPERVISOR', 'EMPLEADO')")
    @Operation(summary = "Actualizar estado de entrega del envío")
    public ResponseEntity<GuiaDespachoResponse> actualizarEstado(
            @PathVariable Long id,
            @RequestParam EstadoEnvio nuevoEstado,
            @RequestParam(required = false) String observaciones) {
        return ResponseEntity.ok(guiaDespachoService.actualizarEstado(id, nuevoEstado, observaciones));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Eliminar guía de despacho")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        guiaDespachoService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
