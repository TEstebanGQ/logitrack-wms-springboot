package com.proyecto.proyectoSpringBoot.controller;

import com.proyecto.proyectoSpringBoot.dto.request.CrearLoteRequest;
import com.proyecto.proyectoSpringBoot.dto.response.LoteResponse;
import com.proyecto.proyectoSpringBoot.model.enums.EstadoLote;
import com.proyecto.proyectoSpringBoot.service.interfaces.ILoteService;
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
@RequestMapping("/api/lotes")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Lotes y Vencimientos", description = "Trazabilidad de lotes, control de caducidad y rotación FIFO/FEFO")
public class LoteController {

    private final ILoteService loteService;

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN','SUPERVISOR','EMPLEADO')")
    @Operation(summary = "Crear o ingresar un nuevo lote de producción")
    public ResponseEntity<LoteResponse> crear(
            @Valid @RequestBody CrearLoteRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        LoteResponse response = loteService.crearLote(request, userDetails.getUsername());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Listar todos los lotes")
    public ResponseEntity<List<LoteResponse>> listar() {
        return ResponseEntity.ok(loteService.listarTodos());
    }

    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Obtener detalle de un lote por ID")
    public ResponseEntity<LoteResponse> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(loteService.obtenerPorId(id));
    }

    @GetMapping("/producto/{productoId}")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Listar lotes de un producto específico")
    public ResponseEntity<List<LoteResponse>> listarPorProducto(@PathVariable Long productoId) {
        return ResponseEntity.ok(loteService.listarPorProducto(productoId));
    }

    @GetMapping("/bodega/{bodegaId}")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Listar lotes en una bodega específica")
    public ResponseEntity<List<LoteResponse>> listarPorBodega(@PathVariable Long bodegaId) {
        return ResponseEntity.ok(loteService.listarPorBodega(bodegaId));
    }

    @GetMapping("/fefo")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Listar lotes ordenados por fecha de vencimiento ascendente (Política FEFO)")
    public ResponseEntity<List<LoteResponse>> listarFEFO(
            @RequestParam Long productoId,
            @RequestParam Long bodegaId) {
        return ResponseEntity.ok(loteService.listarPorProductoYBodegaFEFO(productoId, bodegaId));
    }

    @GetMapping("/proximos-vencer")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Listar lotes con fecha de vencimiento dentro de los próximos N días")
    public ResponseEntity<List<LoteResponse>> listarProximosAVencer(
            @RequestParam(defaultValue = "30") int dias) {
        return ResponseEntity.ok(loteService.listarProximosAVencer(dias));
    }

    @PutMapping("/{id}/estado")
    @PreAuthorize("hasAnyRole('ADMIN','SUPERVISOR')")
    @Operation(summary = "Actualizar el estado de un lote (DISPONIBLE, CUARENTENA, VENCIDO, AGOTADO)")
    public ResponseEntity<LoteResponse> actualizarEstado(
            @PathVariable Long id,
            @RequestParam EstadoLote estado,
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(loteService.actualizarEstado(id, estado, userDetails.getUsername()));
    }
}
