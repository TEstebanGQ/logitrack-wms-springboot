package com.proyecto.proyectoSpringBoot.listener;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.proyecto.proyectoSpringBoot.model.entity.Auditoria;
import com.proyecto.proyectoSpringBoot.model.entity.Usuario;
import com.proyecto.proyectoSpringBoot.model.enums.TipoOperacion;
import jakarta.persistence.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.time.LocalDateTime;

/**
 * Listener de JPA para auditoría automática.
 * Se activa en INSERT, UPDATE y DELETE sobre entidades que lo usen.
 * Nota: se registra manualmente desde cada entidad con @EntityListeners(AuditoriaEntityListener.class)
 */
public class AuditoriaEntityListener {

    private static final ObjectMapper mapper = new ObjectMapper()
            .registerModule(new JavaTimeModule())
            .configure(com.fasterxml.jackson.databind.DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
            .configure(com.fasterxml.jackson.databind.SerializationFeature.FAIL_ON_EMPTY_BEANS, false);

    @PostPersist
    public void afterInsert(Object entity) {
        guardar(entity, TipoOperacion.INSERT, null);
    }

    @PostUpdate
    public void afterUpdate(Object entity) {
        guardar(entity, TipoOperacion.UPDATE, null);
    }

    @PostRemove
    public void afterDelete(Object entity) {
        guardar(entity, TipoOperacion.DELETE, null);
    }

    private void guardar(Object entity, TipoOperacion tipo, String valoresAnteriores) {
        try {
            // Obtener usuario autenticado
            String emailUsuario = obtenerEmailUsuario();
            String valoresNuevos = tipo != TipoOperacion.DELETE
                    ? toJson(entity) : null;

            // Se persiste vía ApplicationContext - configurado como Spring Bean en producción
            AuditoriaRegistrar.registrar(
                    entity.getClass().getSimpleName(),
                    obtenerIdEntidad(entity),
                    tipo,
                    emailUsuario,
                    valoresAnteriores,
                    valoresNuevos
            );
        } catch (Throwable e) {
            // Log silencioso para no interrumpir la operación principal
            System.err.println("[AUDITORIA] Error al registrar: " + e.getMessage());
        }
    }

    private String obtenerEmailUsuario() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated() && !auth.getPrincipal().equals("anonymousUser")) {
            return auth.getName();
        }
        return "Sistema";
    }

    private Long obtenerIdEntidad(Object entity) {
        try {
            var idField = entity.getClass().getDeclaredField("id");
            idField.setAccessible(true);
            Object val = idField.get(entity);
            return val != null ? ((Number) val).longValue() : null;
        } catch (Exception e) {
            return null;
        }
    }

    private String toJson(Object entity) {
        if (entity == null) return null;
        try {
            return mapper.writeValueAsString(entity);
        } catch (Throwable e) {
            return "{\"id\":" + obtenerIdEntidad(entity) + "}";
        }
    }
}
