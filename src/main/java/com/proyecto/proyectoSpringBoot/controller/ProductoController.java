package com.proyecto.proyectoSpringBoot.controller;

import com.proyecto.proyectoSpringBoot.dto.request.CrearProductoRequest;
import com.proyecto.proyectoSpringBoot.dto.response.ProductoResponse;
import com.proyecto.proyectoSpringBoot.service.interfaces.IProductoService;
import io.swagger.v3.oas.annotations.Operation;
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
@RequestMapping("/api/productos")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Productos", description = "CRUD de productos e inventario")
public class ProductoController {

    private final IProductoService productoService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Crear nuevo producto")
    public ResponseEntity<ProductoResponse> crear(@Valid @RequestBody CrearProductoRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(productoService.crear(request));
    }

    @GetMapping
    @Operation(summary = "Listar todos los productos activos")
    public ResponseEntity<List<ProductoResponse>> listar() {
        return ResponseEntity.ok(productoService.listarActivos());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener producto por ID")
    public ResponseEntity<ProductoResponse> obtener(@PathVariable Long id) {
        return ResponseEntity.ok(productoService.obtenerPorId(id));
    }

    @GetMapping("/stock-bajo")
    @Operation(summary = "Productos con stock bajo (por defecto < 10 unidades)")
    public ResponseEntity<List<ProductoResponse>> stockBajo(
            @RequestParam(defaultValue = "10") int umbral) {
        return ResponseEntity.ok(productoService.listarConStockBajo(umbral));
    }

    @GetMapping("/categoria/{categoria}")
    @Operation(summary = "Productos por categoría")
    public ResponseEntity<List<ProductoResponse>> porCategoria(@PathVariable String categoria) {
        return ResponseEntity.ok(productoService.listarPorCategoria(categoria));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Actualizar producto")
    public ResponseEntity<ProductoResponse> actualizar(@PathVariable Long id,
                                                        @Valid @RequestBody CrearProductoRequest request) {
        return ResponseEntity.ok(productoService.actualizar(id, request));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Eliminar producto (soft delete)")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        productoService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
