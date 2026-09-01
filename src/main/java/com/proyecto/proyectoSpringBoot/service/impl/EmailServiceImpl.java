package com.proyecto.proyectoSpringBoot.service.impl;

import com.proyecto.proyectoSpringBoot.model.entity.Usuario;
import com.proyecto.proyectoSpringBoot.model.enums.RolUsuario;
import com.proyecto.proyectoSpringBoot.service.interfaces.IEmailService;
import jakarta.mail.internet.MimeMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;

@Slf4j
@Service
public class EmailServiceImpl implements IEmailService {

    private final JavaMailSender mailSender;
    private final EmailTemplateBuilder templateBuilder;
    private final com.proyecto.proyectoSpringBoot.repository.UsuarioRepository usuarioRepository;

    @Value("${app.mail.enabled:true}")
    private boolean mailEnabled;

    @Value("${app.mail.from:no-reply@logitrack.com}")
    private String mailFrom;

    @Value("${spring.mail.username:}")
    private String smtpUsername;

    public EmailServiceImpl(ObjectProvider<JavaMailSender> mailSenderProvider,
                            EmailTemplateBuilder templateBuilder,
                            com.proyecto.proyectoSpringBoot.repository.UsuarioRepository usuarioRepository) {
        this.mailSender = mailSenderProvider.getIfAvailable();
        this.templateBuilder = templateBuilder;
        this.usuarioRepository = usuarioRepository;
    }

    @Override
    @Async("mailTaskExecutor")
    public void enviarCorreoBienvenida(Usuario usuario) {
        if (usuario == null || usuario.getEmail() == null || usuario.getEmail().isBlank()) {
            log.warn("[EmailService] No se puede enviar correo de bienvenida: datos de usuario inválidos.");
            return;
        }
        String nombreCompleto = ((usuario.getNombre() != null ? usuario.getNombre() : "") + " " +
                (usuario.getApellido() != null ? usuario.getApellido() : "")).trim();
        if (nombreCompleto.isBlank()) {
            nombreCompleto = usuario.getEmail();
        }
        enviarCorreoBienvenida(nombreCompleto, usuario.getEmail(), usuario.getRol());
    }

    @Override
    @Async("mailTaskExecutor")
    public void enviarCorreoBienvenida(String nombreCompleto, String email, RolUsuario rol) {
        if (!mailEnabled) {
            log.info("[EmailService] Envío de correos deshabilitado por configuración (app.mail.enabled=false). Omitiendo para: {}", email);
            return;
        }

        if (mailSender == null) {
            log.warn("[EmailService] JavaMailSender no disponible en el contexto. Verifica la configuración SMTP en application.properties.");
            return;
        }

        if (email == null || email.isBlank()) {
            log.warn("[EmailService] Dirección de correo destino vacía. Omitiendo envío.");
            return;
        }

        try {
            log.info("[EmailService] Preparando correo de bienvenida para '{}' ({}) con rol: {}", nombreCompleto, email, rol);

            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, MimeMessageHelper.MULTIPART_MODE_MIXED_RELATED, StandardCharsets.UTF_8.name());

            String remitente = (smtpUsername != null && !smtpUsername.isBlank()) ? smtpUsername : mailFrom;
            helper.setFrom(remitente, "LogiTrack S.A. | Notificaciones");
            helper.setTo(email);
            helper.setSubject(templateBuilder.buildWelcomeSubject(rol));

            String htmlBody = templateBuilder.buildWelcomeEmailHtml(nombreCompleto, email, rol);
            helper.setText(htmlBody, true);

            mailSender.send(message);
            log.info("[EmailService] Correo de bienvenida enviado exitosamente a: {}", email);

        } catch (Exception e) {
            log.error("[EmailService] Error al enviar correo de bienvenida a '{}' ({}): {}", nombreCompleto, email, e.getMessage());
            // No propagamos la excepción para evitar interrumpir flujos de registro del usuario
        }
    }

    @Override
    @Async("mailTaskExecutor")
    public void notificarRegistroASuperAdmin(Usuario nuevoUsuario) {
        if (!mailEnabled || nuevoUsuario == null) return;

        java.util.List<Usuario> superAdmins = usuarioRepository.findByRol(RolUsuario.SUPER_ADMIN);
        if (superAdmins.isEmpty()) {
            superAdmins = usuarioRepository.findByRol(RolUsuario.ADMIN);
        }

        String nombreNuevo = ((nuevoUsuario.getNombre() != null ? nuevoUsuario.getNombre() : "") + " " +
                (nuevoUsuario.getApellido() != null ? nuevoUsuario.getApellido() : "")).trim();
        if (nombreNuevo.isBlank()) nombreNuevo = nuevoUsuario.getEmail();

        for (Usuario admin : superAdmins) {
            try {
                if (mailSender != null && admin.getEmail() != null) {
                    MimeMessage message = mailSender.createMimeMessage();
                    MimeMessageHelper helper = new MimeMessageHelper(message, MimeMessageHelper.MULTIPART_MODE_MIXED_RELATED, StandardCharsets.UTF_8.name());

                    String remitente = (smtpUsername != null && !smtpUsername.isBlank()) ? smtpUsername : mailFrom;
                    helper.setFrom(remitente, "LogiTrack S.A. | Control de Acceso");
                    helper.setTo(admin.getEmail());
                    helper.setSubject("🔔 Alerta de Registro: " + nombreNuevo + " fue registrado en el sistema");

                    String htmlBody = templateBuilder.buildSuperAdminNotificationHtml(admin.getNombre(), nombreNuevo, nuevoUsuario.getEmail());
                    helper.setText(htmlBody, true);

                    mailSender.send(message);
                    log.info("[EmailService] Notificación enviada a Super Admin: {}", admin.getEmail());
                }
            } catch (Exception e) {
                log.error("[EmailService] Error enviando correo a Super Admin {}: {}", admin.getEmail(), e.getMessage());
            }
        }
    }
}
