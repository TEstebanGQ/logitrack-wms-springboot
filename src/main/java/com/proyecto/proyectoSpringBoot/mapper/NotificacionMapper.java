package com.proyecto.proyectoSpringBoot.mapper;

import com.proyecto.proyectoSpringBoot.dto.response.NotificacionResponse;
import com.proyecto.proyectoSpringBoot.model.entity.Notificacion;
import org.springframework.stereotype.Component;

@Component
public class NotificacionMapper {
    public NotificacionResponse toResponse(Notificacion n) {
        if (n == null) return null;
        NotificacionResponse r = new NotificacionResponse();
        r.setId(n.getId());
        r.setTitulo(n.getTitulo());
        r.setMensaje(n.getMensaje());
        r.setTipo(n.getTipo() != null ? n.getTipo().name() : null);
        r.setLeida(n.isLeida());
        r.setFechaCreacion(n.getFechaCreacion());
        r.setFechaLectura(n.getFechaLectura());
        r.setUrlAccion(n.getUrlAccion());
        return r;
    }
}
