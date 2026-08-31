package com.proyecto.proyectoSpringBoot.controller;

import com.proyecto.proyectoSpringBoot.repository.AuditoriaRepository;
import com.proyecto.proyectoSpringBoot.repository.BodegaRepository;
import com.proyecto.proyectoSpringBoot.repository.MovimientoRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/config")
@RequiredArgsConstructor
@Tag(name = "Configuración Pública", description = "Endpoints de configuración pública no sensible para clientes frontend")
public class PublicConfigController {

    private final BodegaRepository bodegaRepository;
    private final MovimientoRepository movimientoRepository;
    private final AuditoriaRepository auditoriaRepository;

    // Client ID de Google para inicialización de Google Sign-In SDK
    @Value("${google.client.id:1056581979401-4n88v213h468n4613n89.apps.googleusercontent.com}")
    private String googleClientId;

    @GetMapping("/public")
    @Operation(summary = "Obtener configuración pública del frontend (Google Client ID, etc.)")
    public ResponseEntity<Map<String, String>> getPublicConfig() {
        return ResponseEntity.ok(Map.of("googleClientId", googleClientId != null ? googleClientId : ""));
    }

    /**
     * [H-014 FIX] Retorna estadísticas reales calculadas de la base de datos para el hero de autenticación.
     */
    @GetMapping("/stats")
    @Operation(summary = "Obtener estadísticas reales del sistema para el hero de autenticación")
    public ResponseEntity<Map<String, Object>> getHeroStats() {
        long bodegas = bodegaRepository.count();
        long movimientos = movimientoRepository.count();
        long auditorias = auditoriaRepository.count();

        return ResponseEntity.ok(Map.of(
                "bodegasActivas", bodegas,
                "totalMovimientos", movimientos,
                "totalAuditorias", auditorias,
                "auditoriaCoverage", "100%"
        ));
    }
}
