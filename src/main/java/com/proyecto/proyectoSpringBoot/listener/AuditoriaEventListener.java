package com.proyecto.proyectoSpringBoot.listener;

import com.proyecto.proyectoSpringBoot.event.AuditoriaEvent;
import com.proyecto.proyectoSpringBoot.model.entity.Auditoria;
import com.proyecto.proyectoSpringBoot.model.entity.Usuario;
import com.proyecto.proyectoSpringBoot.repository.AuditoriaRepository;
import com.proyecto.proyectoSpringBoot.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Slf4j
@Component
@RequiredArgsConstructor
public class AuditoriaEventListener {

    private final AuditoriaRepository auditoriaRepository;
    private final UsuarioRepository usuarioRepository;

    @EventListener
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void handleAuditoriaEvent(AuditoriaEvent event) {
        try {
            Usuario usuario = null;
            if (event.getEmailUsuario() != null && !event.getEmailUsuario().equals("Sistema")) {
                usuario = usuarioRepository.findByEmail(event.getEmailUsuario()).orElse(null);
            }
            Auditoria audit = Auditoria.builder()
                    .entidad(event.getEntidad())
                    .entidadId(event.getEntidadId())
                    .tipoOperacion(event.getTipoOperacion())
                    .fechaHora(LocalDateTime.now())
                    .usuario(usuario)
                    .valoresAnteriores(event.getValoresAnteriores())
                    .valoresNuevos(event.getValoresNuevos())
                    .descripcion(event.getDescripcion() != null ? event.getDescripcion() :
                            event.getTipoOperacion().name() + " en " + event.getEntidad() + (event.getEntidadId() != null ? " id=" + event.getEntidadId() : ""))
                    .build();
            auditoriaRepository.save(audit);
        } catch (Exception e) {
            log.error("[AUDITORIA] Error guardando registro de auditoría: {}", e.getMessage(), e);
        }
    }
}
