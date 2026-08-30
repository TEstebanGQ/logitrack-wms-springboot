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
}
