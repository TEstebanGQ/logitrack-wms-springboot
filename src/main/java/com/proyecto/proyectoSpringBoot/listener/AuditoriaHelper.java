package com.proyecto.proyectoSpringBoot.listener;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.proyecto.proyectoSpringBoot.event.AuditoriaEvent;
import com.proyecto.proyectoSpringBoot.model.enums.TipoOperacion;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

/**
 * [H-008, H-009, H-010 FIX] Helper compartido para centralizar la auditoría de eventos de negocio.
 * Inyecta ObjectMapper gestionado por el contenedor de Spring y registra logs de advertencia ante cualquier error.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class AuditoriaHelper {

    private final ApplicationEventPublisher eventPublisher;
    private final ObjectMapper objectMapper;

    /**
     * Publica un evento de auditoría asíncrono infiriendo el usuario autenticado actual.
     */
    public void publishAudit(String entidad, Long entidadId, TipoOperacion tipo, String valoresAnteriores, String valoresNuevos, String descripcion) {
        publishAudit(entidad, entidadId, tipo, valoresAnteriores, valoresNuevos, descripcion, getCurrentUserEmail());
    }

    /**
     * Publica un evento de auditoría asíncrono con email de usuario explícito.
     */
    public void publishAudit(String entidad, Long entidadId, TipoOperacion tipo, String valoresAnteriores, String valoresNuevos, String descripcion, String emailUsuario) {
        try {
            eventPublisher.publishEvent(AuditoriaEvent.builder()
                    .entidad(entidad)
                    .entidadId(entidadId)
                    .tipoOperacion(tipo)
                    .emailUsuario(emailUsuario != null ? emailUsuario : getCurrentUserEmail())
                    .valoresAnteriores(valoresAnteriores)
                    .valoresNuevos(valoresNuevos)
                    .descripcion(descripcion)
                    .build());
        } catch (Exception e) {
            log.warn("[AUDITORIA] Fallo al publicar evento de auditoría para entidad {} (id={}): {}", entidad, entidadId, e.getMessage(), e);
        }
    }

    /**
     * Obtiene el email del usuario autenticado en el contexto de seguridad.
     */
    public String getCurrentUserEmail() {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth != null && auth.isAuthenticated() && !"anonymousUser".equals(auth.getPrincipal())) {
                return auth.getName();
            }
        } catch (Exception e) {
            log.warn("[AUDITORIA] Error al recuperar usuario autenticado del SecurityContext: {}", e.getMessage());
        }
        return "admin@logitrack.com";
    }

    /**
     * Serializa un objeto a JSON de manera segura usando el ObjectMapper inyectado.
     */
    public String toJson(Object obj) {
        if (obj == null) return null;
        try {
            return objectMapper.writeValueAsString(obj);
        } catch (Exception e) {
            log.warn("[AUDITORIA] Error serializando objeto a JSON para auditoría: {}", e.getMessage(), e);
            return null;
        }
    }
}
