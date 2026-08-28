package com.proyecto.proyectoSpringBoot.controller;

import com.proyecto.proyectoSpringBoot.dto.request.ConteoCiclicoRequest;
import com.proyecto.proyectoSpringBoot.dto.response.ConteoCiclicoResponse;
import com.proyecto.proyectoSpringBoot.model.enums.EstadoConteo;
import com.proyecto.proyectoSpringBoot.service.interfaces.IConteoCiclicoService;
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
@RequestMapping("/api/conteos-ciclicos")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Conteos Cíclicos e Inventario Físico", description = "Auditoría rotativa, verificación de stock físico y conciliación automática")
public class ConteoCiclicoController {

    private final IConteoCiclicoService conteoCiclicoService;

    @GetMapping
    @Operation(summary = "Listar todos los conteos cíclicos")
    public ResponseEntity<List<ConteoCiclicoResponse>> listarTodos() {
        return ResponseEntity.ok(conteoCiclicoService.listarTodos());
    }

    @GetMapping("/bodega/{bodegaId}")
    @Operation(summary = "Listar conteos por bodega")
    public ResponseEntity<List<ConteoCiclicoResponse>> listarPorBodega(@PathVariable Long bodegaId) {
        return ResponseEntity.ok(conteoCiclicoService.listarPorBodega(bodegaId));
    }

    @GetMapping("/estado/{estado}")
    @Operation(summary = "Listar conteos por estado")
    public ResponseEntity<List<ConteoCiclicoResponse>> listarPorEstado(@PathVariable EstadoConteo estado) {
        return ResponseEntity.ok(conteoCiclicoService.listarPorEstado(estado));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener conteo cíclico por ID con detalles")
    public ResponseEntity<ConteoCiclicoResponse> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(conteoCiclicoService.obtenerPorId(id));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPERVISOR')")
    @Operation(summary = "Programar nuevo conteo cíclico")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Conteo programado exitosamente"),
        @ApiResponse(responseCode = "400", description = "Datos inválidos")
    })
    public ResponseEntity<ConteoCiclicoResponse> crear(
            @Valid @RequestBody ConteoCiclicoRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        String email = userDetails != null ? userDetails.getUsername() : "admin@logitrack.com";
        return ResponseEntity.status(HttpStatus.CREATED).body(conteoCiclicoService.crear(request, email));
    }

    @PatchMapping("/{id}/detalles/{detalleId}/contar")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPERVISOR', 'EMPLEADO')")
    @Operation(summary = "Registrar conteo físico en una línea de auditoría")
    public ResponseEntity<ConteoCiclicoResponse> registrarConteoFisico(
            @PathVariable Long id,
            @PathVariable Long detalleId,
            @RequestParam Integer stockFisico,
            @RequestParam(required = false) String notas) {
        return ResponseEntity.ok(conteoCiclicoService.registrarConteoFisico(id, detalleId, stockFisico, notas));
    }

    @PostMapping("/{id}/conciliar")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPERVISOR')")
    @Operation(summary = "Conciliar diferencias y generar ajustes de inventario automáticos")
    public ResponseEntity<ConteoCiclicoResponse> conciliar(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails) {
        String email = userDetails != null ? userDetails.getUsername() : "admin@logitrack.com";
        return ResponseEntity.ok(conteoCiclicoService.conciliarYCerrar(id, email));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Eliminar conteo cíclico")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        conteoCiclicoService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
