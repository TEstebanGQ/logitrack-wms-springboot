package com.proyecto.proyectoSpringBoot.service.interfaces;

import com.proyecto.proyectoSpringBoot.model.entity.Usuario;
import com.proyecto.proyectoSpringBoot.model.enums.RolUsuario;

public interface IEmailService {

    /**
     * Envía un correo electrónico de bienvenida profesional adaptado al rol del usuario registrado.
     *
     * @param usuario Entidad del usuario que acaba de registrarse.
     */
    void enviarCorreoBienvenida(Usuario usuario);

    /**
     * Envía un correo electrónico de bienvenida con los datos explícitos del destinatario.
     *
     * @param nombreCompleto Nombre y apellido del usuario.
     * @param email          Dirección de correo destino.
     * @param rol            Rol asignado en el sistema LogiTrack.
     */
    void enviarCorreoBienvenida(String nombreCompleto, String email, RolUsuario rol);

    /**
     * Envía una notificación por correo al Super Admin informando del registro de un nuevo usuario.
     *
     * @param nuevoUsuario Entidad del usuario que acaba de registrarse.
     */
    void notificarRegistroASuperAdmin(Usuario nuevoUsuario);

    /**
     * Envía una notificación por correo al usuario informándole que su rol ha sido actualizado.
     *
     * @param usuario     Entidad del usuario afectado.
     * @param rolAnterior Rol que poseía anteriormente.
     * @param nuevoRol    Nuevo rol asignado por el administrador.
     */
    void notificarCambioRol(Usuario usuario, RolUsuario rolAnterior, RolUsuario nuevoRol);

    /**
     * Envía el reporte ejecutivo diario por correo electrónico a la lista de administradores/gerentes.
     *
     * @param destinatarios Lista de usuarios que recibirán el reporte.
     * @param resumen       DTO con el resumen consolidado de operaciones y auditorías del día.
     */
    void enviarReporteDiario(java.util.List<Usuario> destinatarios, com.proyecto.proyectoSpringBoot.dto.response.ReporteDiarioDTO resumen);
}
