package com.proyecto.proyectoSpringBoot.controller;

import com.proyecto.proyectoSpringBoot.dto.request.MovimientoRequest;
import com.proyecto.proyectoSpringBoot.dto.response.MovimientoResponse;
import com.proyecto.proyectoSpringBoot.model.enums.TipoMovimiento;
import com.proyecto.proyectoSpringBoot.service.interfaces.IMovimientoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/movimientos")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Movimientos", description = "Registro de entradas, salidas y transferencias")
public class MovimientoController {

    private final IMovimientoService movimientoService;

    @PostMapping
    @org.springframework.security.access.prepost.PreAuthorize("hasAnyRole('ADMIN', 'SUPERVISOR', 'JEFE_COMPRAS', 'EMPLEADO')")
    @Operation(summary = "Registrar un movimiento de inventario")
    public ResponseEntity<MovimientoResponse> registrar(
            @Valid @RequestBody MovimientoRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(movimientoService.registrar(request, userDetails.getUsername()));
    }

    @GetMapping
    @Operation(summary = "Listar todos los movimientos con paginación")
    public ResponseEntity<Page<MovimientoResponse>> listar(
            @PageableDefault(size = 20, sort = "fecha", direction = Sort.Direction.DESC) Pageable pageable) {
        return ResponseEntity.ok(movimientoService.listarTodos(pageable));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener movimiento por ID")
    public ResponseEntity<MovimientoResponse> obtener(@PathVariable Long id) {
        return ResponseEntity.ok(movimientoService.obtenerPorId(id));
    }

    @GetMapping("/tipo/{tipo}")
    @Operation(summary = "Filtrar por tipo de movimiento")
    public ResponseEntity<List<MovimientoResponse>> porTipo(@PathVariable TipoMovimiento tipo) {
        return ResponseEntity.ok(movimientoService.listarPorTipo(tipo));
    }

    @GetMapping("/fechas")
    @Operation(summary = "Filtrar por rango de fechas")
    public ResponseEntity<List<MovimientoResponse>> porFechas(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime inicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fin) {
        return ResponseEntity.ok(movimientoService.listarPorRangoFechas(inicio, fin));
    }

    @GetMapping("/usuario/{usuarioId}")
    @Operation(summary = "Filtrar por usuario responsable")
    public ResponseEntity<List<MovimientoResponse>> porUsuario(@PathVariable Long usuarioId) {
        return ResponseEntity.ok(movimientoService.listarPorUsuario(usuarioId));
    }

    @GetMapping("/bodega/{bodegaId}")
    @Operation(summary = "Filtrar por bodega (origen o destino)")
    public ResponseEntity<List<MovimientoResponse>> porBodega(@PathVariable Long bodegaId) {
        return ResponseEntity.ok(movimientoService.listarPorBodega(bodegaId));
    }
}
