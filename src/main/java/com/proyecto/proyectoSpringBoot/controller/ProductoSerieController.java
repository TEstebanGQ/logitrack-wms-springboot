package com.proyecto.proyectoSpringBoot.controller;

import com.proyecto.proyectoSpringBoot.dto.request.ProductoSerieRequest;
import com.proyecto.proyectoSpringBoot.dto.response.ProductoSerieResponse;
import com.proyecto.proyectoSpringBoot.model.enums.EstadoSerie;
import com.proyecto.proyectoSpringBoot.service.interfaces.IProductoSerieService;
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
@RequestMapping("/api/series")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Series de Productos", description = "Trazabilidad de números de serie unitarios")
public class ProductoSerieController {

    private final IProductoSerieService productoSerieService;

    @GetMapping
    @Operation(summary = "Listar todas las series")
    public ResponseEntity<List<ProductoSerieResponse>> listarTodas() {
        return ResponseEntity.ok(productoSerieService.listarTodas());
    }

    @GetMapping("/producto/{productoId}")
    @Operation(summary = "Listar series por producto")
    public ResponseEntity<List<ProductoSerieResponse>> listarPorProducto(@PathVariable Long productoId) {
        return ResponseEntity.ok(productoSerieService.listarPorProducto(productoId));
    }

    @GetMapping("/bodega/{bodegaId}")
    @Operation(summary = "Listar series por bodega")
    public ResponseEntity<List<ProductoSerieResponse>> listarPorBodega(@PathVariable Long bodegaId) {
        return ResponseEntity.ok(productoSerieService.listarPorBodega(bodegaId));
    }

    @GetMapping("/estado/{estado}")
    @Operation(summary = "Listar series por estado")
    public ResponseEntity<List<ProductoSerieResponse>> listarPorEstado(@PathVariable EstadoSerie estado) {
        return ResponseEntity.ok(productoSerieService.listarPorEstado(estado));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener serie por ID")
    public ResponseEntity<ProductoSerieResponse> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(productoSerieService.obtenerPorId(id));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPERVISOR', 'EMPLEADO')")
    @Operation(summary = "Registrar nuevo número de serie")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Serie registrada exitosamente"),
        @ApiResponse(responseCode = "400", description = "Datos inválidos")
    })
    public ResponseEntity<ProductoSerieResponse> registrar(@Valid @RequestBody ProductoSerieRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(productoSerieService.registrar(request));
    }

    @PatchMapping("/{id}/estado")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPERVISOR', 'EMPLEADO')")
    @Operation(summary = "Actualizar estado de número de serie")
    public ResponseEntity<ProductoSerieResponse> actualizarEstado(
            @PathVariable Long id,
            @RequestParam EstadoSerie nuevoEstado,
            @RequestParam(required = false) String observaciones) {
        return ResponseEntity.ok(productoSerieService.actualizarEstado(id, nuevoEstado, observaciones));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Dar de baja número de serie")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        productoSerieService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
