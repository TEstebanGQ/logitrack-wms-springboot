package com.proyecto.proyectoSpringBoot.controller;

import com.proyecto.proyectoSpringBoot.dto.response.AuditoriaResponse;
import com.proyecto.proyectoSpringBoot.model.enums.TipoOperacion;
import com.proyecto.proyectoSpringBoot.service.interfaces.IAuditoriaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/auditorias")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('ADMIN', 'SUPERVISOR', 'GERENTE_LOGISTICA')")
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Auditorías", description = "Consulta de registros de auditoría")
public class AuditoriaController {

    private final IAuditoriaService auditoriaService;

    @GetMapping
    @Operation(summary = "Listar todas las auditorías con paginación")
    public ResponseEntity<Page<AuditoriaResponse>> listar(
            @PageableDefault(size = 20, sort = "fechaHora", direction = Sort.Direction.DESC) Pageable pageable) {
        return ResponseEntity.ok(auditoriaService.listarTodas(pageable));
    }

    @GetMapping("/usuario/{usuarioId}")
    @Operation(summary = "Auditorías por usuario")
    public ResponseEntity<List<AuditoriaResponse>> porUsuario(@PathVariable Long usuarioId) {
        return ResponseEntity.ok(auditoriaService.listarPorUsuario(usuarioId));
    }

    @GetMapping("/operacion/{tipo}")
    @Operation(summary = "Auditorías por tipo de operación (INSERT, UPDATE, DELETE)")
    public ResponseEntity<List<AuditoriaResponse>> porOperacion(@PathVariable TipoOperacion tipo) {
        return ResponseEntity.ok(auditoriaService.listarPorTipoOperacion(tipo));
    }

    @GetMapping("/entidad/{entidad}")
    @Operation(summary = "Auditorías por entidad afectada")
    public ResponseEntity<List<AuditoriaResponse>> porEntidad(@PathVariable String entidad) {
        return ResponseEntity.ok(auditoriaService.listarPorEntidad(entidad));
    }

    @GetMapping("/fechas")
    @Operation(summary = "Auditorías por rango de fechas")
    public ResponseEntity<List<AuditoriaResponse>> porFechas(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime inicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fin) {
        return ResponseEntity.ok(auditoriaService.listarPorRangoFechas(inicio, fin));
    }
}
