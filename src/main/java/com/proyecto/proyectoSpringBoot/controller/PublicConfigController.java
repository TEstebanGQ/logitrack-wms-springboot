package com.proyecto.proyectoSpringBoot.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/config")
@Tag(name = "Configuración Pública", description = "Endpoints de configuración pública no sensible para clientes frontend")
public class PublicConfigController {

    @Value("${google.client.id:1056581979401-4n88v213h468n4613n89.apps.googleusercontent.com}")
    private String googleClientId;

    @GetMapping("/public")
    @Operation(summary = "Obtener configuración pública del frontend (Google Client ID, etc.)")
    public ResponseEntity<Map<String, String>> getPublicConfig() {
        return ResponseEntity.ok(Map.of("googleClientId", googleClientId));
    }
}
