package com.proyecto.proyectoSpringBoot.controller;

import com.proyecto.proyectoSpringBoot.dto.request.CrearAjusteRequest;
import com.proyecto.proyectoSpringBoot.dto.response.AjusteResponse;
import com.proyecto.proyectoSpringBoot.model.enums.TipoAjuste;
import com.proyecto.proyectoSpringBoot.service.interfaces.IAjusteInventarioService;
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
@RequestMapping("/api/ajustes")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Ajustes de Inventario", description = "Gestión de mermas, daños, conteos físicos y regularizaciones de stock")
public class AjusteInventarioController {

    private final IAjusteInventarioService ajusteService;

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN','SUPERVISOR','GERENTE_LOGISTICA')")
    @Operation(summary = "Registrar un ajuste o merma de inventario")
    public ResponseEntity<AjusteResponse> registrar(
            @Valid @RequestBody CrearAjusteRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        AjusteResponse response = ajusteService.registrarAjuste(request, userDetails.getUsername());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Listar todos los ajustes de inventario ordenados por fecha descendente")
    public ResponseEntity<List<AjusteResponse>> listar() {
        return ResponseEntity.ok(ajusteService.listarTodos());
    }

    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Obtener detalle de un ajuste por ID")
    public ResponseEntity<AjusteResponse> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(ajusteService.obtenerPorId(id));
    }

    @GetMapping("/bodega/{bodegaId}")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Listar ajustes de una bodega específica")
    public ResponseEntity<List<AjusteResponse>> listarPorBodega(@PathVariable Long bodegaId) {
        return ResponseEntity.ok(ajusteService.listarPorBodega(bodegaId));
    }

    @GetMapping("/producto/{productoId}")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Listar ajustes de un producto específico")
    public ResponseEntity<List<AjusteResponse>> listarPorProducto(@PathVariable Long productoId) {
        return ResponseEntity.ok(ajusteService.listarPorProducto(productoId));
    }

    @GetMapping("/tipo/{tipo}")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Listar ajustes por tipo (MERMA, DANO, CONTEO_FISICO, VENCIMIENTO, OTRO)")
    public ResponseEntity<List<AjusteResponse>> listarPorTipo(@PathVariable TipoAjuste tipo) {
        return ResponseEntity.ok(ajusteService.listarPorTipo(tipo));
    }
}
