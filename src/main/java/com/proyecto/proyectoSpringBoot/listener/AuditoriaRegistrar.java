package com.proyecto.proyectoSpringBoot.listener;

import com.proyecto.proyectoSpringBoot.event.AuditoriaEvent;
import com.proyecto.proyectoSpringBoot.model.enums.TipoOperacion;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

/**
 * Componente estático auxiliar que publica AuditoriaEvent
 * evitando llamadas síncronas al repositorio en callbacks JPA.
 */
@Component
public class AuditoriaRegistrar {

    private static ApplicationEventPublisher eventPublisher;

    @Autowired
    public AuditoriaRegistrar(ApplicationEventPublisher eventPublisher) {
        AuditoriaRegistrar.eventPublisher = eventPublisher;
    }

    public static void registrar(String entidad, Long entidadId, TipoOperacion tipo,
                                  String emailUsuario, String valoresAnt, String valoresNuevos) {
        try {
            if (eventPublisher != null) {
                eventPublisher.publishEvent(AuditoriaEvent.builder()
                        .entidad(entidad)
                        .entidadId(entidadId)
                        .tipoOperacion(tipo)
                        .emailUsuario(emailUsuario)
                        .valoresAnteriores(valoresAnt)
                        .valoresNuevos(valoresNuevos)
                        .build());
            }
        } catch (Exception e) {
            System.err.println("[AUDITORIA] No se pudo publicar evento: " + e.getMessage());
        }
    }
}
