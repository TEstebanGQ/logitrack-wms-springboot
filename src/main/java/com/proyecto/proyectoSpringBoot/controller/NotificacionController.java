package com.proyecto.proyectoSpringBoot.controller;

import com.proyecto.proyectoSpringBoot.dto.response.ContadorNotificacionesResponse;
import com.proyecto.proyectoSpringBoot.dto.response.NotificacionResponse;
import com.proyecto.proyectoSpringBoot.service.interfaces.INotificacionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/notificaciones")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Notificaciones", description = "Centro de notificaciones de usuario y alertas del sistema")
public class NotificacionController {

    private final INotificacionService notificacionService;

    @GetMapping
    @Operation(summary = "Obtener todas las notificaciones del usuario autenticado")
    public ResponseEntity<List<NotificacionResponse>> misNotificaciones(
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(notificacionService.misNotificaciones(userDetails.getUsername()));
    }

    @GetMapping("/no-leidas/contador")
    @Operation(summary = "Obtener conteo de notificaciones no leídas")
    public ResponseEntity<ContadorNotificacionesResponse> contarNoLeidas(
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(notificacionService.contarNoLeidas(userDetails.getUsername()));
    }

    @PutMapping("/{id}/leer")
    @Operation(summary = "Marcar una notificación específica como leída")
    public ResponseEntity<Void> marcarLeida(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails) {
        notificacionService.marcarLeida(id, userDetails.getUsername());
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/leer-todas")
    @Operation(summary = "Marcar todas las notificaciones del usuario como leídas")
    public ResponseEntity<Void> marcarTodasLeidas(
            @AuthenticationPrincipal UserDetails userDetails) {
        notificacionService.marcarTodasLeidas(userDetails.getUsername());
        return ResponseEntity.noContent().build();
    }
}
