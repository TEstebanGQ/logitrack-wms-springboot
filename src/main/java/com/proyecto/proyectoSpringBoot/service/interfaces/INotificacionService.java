package com.proyecto.proyectoSpringBoot.service.interfaces;

import com.proyecto.proyectoSpringBoot.dto.response.ContadorNotificacionesResponse;
import com.proyecto.proyectoSpringBoot.dto.response.NotificacionResponse;
import com.proyecto.proyectoSpringBoot.model.enums.TipoNotificacion;
import java.util.List;

public interface INotificacionService {
    void enviar(Long usuarioId, String titulo, String mensaje, TipoNotificacion tipo);
    void enviarATodosLosAdmins(String titulo, String mensaje, TipoNotificacion tipo);
    List<NotificacionResponse> misNotificaciones(String emailUsuario);
    ContadorNotificacionesResponse contarNoLeidas(String emailUsuario);
    void marcarLeida(Long id, String emailUsuario);
    void marcarTodasLeidas(String emailUsuario);
    void limpiarNotificacionesObsoletas(String nombreProducto, String nombreBodega);
}
