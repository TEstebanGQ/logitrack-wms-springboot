package com.proyecto.proyectoSpringBoot.controller;

import com.proyecto.proyectoSpringBoot.dto.request.TareaPickingRequest;
import com.proyecto.proyectoSpringBoot.dto.response.TareaPickingResponse;
import com.proyecto.proyectoSpringBoot.model.enums.EstadoPicking;
import com.proyecto.proyectoSpringBoot.service.interfaces.ITareaPickingService;
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
@RequestMapping("/api/picking")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Picking y Recolección", description = "Monitor y asignación de órdenes de preparación para operarios")
public class TareaPickingController {

    private final ITareaPickingService tareaPickingService;

    @GetMapping
    @Operation(summary = "Listar todas las tareas de picking")
    public ResponseEntity<List<TareaPickingResponse>> listarTodas() {
        return ResponseEntity.ok(tareaPickingService.listarTodas());
    }

    @GetMapping("/pedido/{pedidoId}")
    @Operation(summary = "Listar tareas de picking por pedido")
    public ResponseEntity<List<TareaPickingResponse>> listarPorPedido(@PathVariable Long pedidoId) {
        return ResponseEntity.ok(tareaPickingService.listarPorPedido(pedidoId));
    }

    @GetMapping("/usuario/{usuarioId}")
    @Operation(summary = "Listar tareas asignadas a un usuario")
    public ResponseEntity<List<TareaPickingResponse>> listarPorUsuario(@PathVariable Long usuarioId) {
        return ResponseEntity.ok(tareaPickingService.listarPorUsuario(usuarioId));
    }

    @GetMapping("/estado/{estado}")
    @Operation(summary = "Listar tareas por estado")
    public ResponseEntity<List<TareaPickingResponse>> listarPorEstado(@PathVariable EstadoPicking estado) {
        return ResponseEntity.ok(tareaPickingService.listarPorEstado(estado));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener tarea de picking por ID")
    public ResponseEntity<TareaPickingResponse> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(tareaPickingService.obtenerPorId(id));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPERVISOR')")
    @Operation(summary = "Crear tarea de picking manual")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Tarea creada exitosamente"),
        @ApiResponse(responseCode = "400", description = "Datos inválidos")
    })
    public ResponseEntity<TareaPickingResponse> crear(@Valid @RequestBody TareaPickingRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(tareaPickingService.crear(request));
    }

    @PatchMapping("/{id}/recolectar")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPERVISOR', 'EMPLEADO')")
    @Operation(summary = "Actualizar cantidad recolectada por el operario")
    public ResponseEntity<TareaPickingResponse> recolectar(
            @PathVariable Long id,
            @RequestParam Integer cantidadRecogida,
            @RequestParam(required = false) String notas) {
        return ResponseEntity.ok(tareaPickingService.actualizarRecoleccion(id, cantidadRecogida, notas));
    }

    @PatchMapping("/{id}/estado")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPERVISOR', 'EMPLEADO')")
    @Operation(summary = "Cambiar estado de la tarea de picking")
    public ResponseEntity<TareaPickingResponse> cambiarEstado(
            @PathVariable Long id,
            @RequestParam EstadoPicking nuevoEstado) {
        return ResponseEntity.ok(tareaPickingService.cambiarEstado(id, nuevoEstado));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPERVISOR')")
    @Operation(summary = "Cancelar tarea de picking")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        tareaPickingService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
