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
import java.util.List;

@Slf4j
@Service
public class EmailServiceImpl implements IEmailService {

    private final JavaMailSender mailSender;
    private final EmailTemplateBuilder templateBuilder;
    private final com.proyecto.proyectoSpringBoot.repository.UsuarioRepository usuarioRepository;
    private final com.proyecto.proyectoSpringBoot.service.interfaces.INotificacionService notificacionService;

    @Value("${app.mail.enabled:true}")
    private boolean mailEnabled;

    @Value("${app.mail.from:no-reply@logitrack.com}")
    private String mailFrom;

    @Value("${spring.mail.username:}")
    private String smtpUsername;

    public EmailServiceImpl(ObjectProvider<JavaMailSender> mailSenderProvider,
                            EmailTemplateBuilder templateBuilder,
                            com.proyecto.proyectoSpringBoot.repository.UsuarioRepository usuarioRepository,
                            com.proyecto.proyectoSpringBoot.service.interfaces.INotificacionService notificacionService) {
        this.mailSender = mailSenderProvider.getIfAvailable();
        this.templateBuilder = templateBuilder;
        this.usuarioRepository = usuarioRepository;
        this.notificacionService = notificacionService;
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

        // Generar notificación interna en el header del nuevo usuario
        try {
            notificacionService.enviar(
                usuario.getId(),
                "🎉 ¡Bienvenido a LogiTrack S.A.!",
                "Hola " + nombreCompleto + ", tu cuenta ha sido configurada con éxito con el rol de " + templateBuilder.getRoleDisplayName(usuario.getRol()) + ".",
                com.proyecto.proyectoSpringBoot.model.enums.TipoNotificacion.INFO
            );
        } catch (Exception e) {
            log.error("[EmailService] Error generando notificación interna de bienvenida: {}", e.getMessage());
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
        }
    }

    @Override
    @Async("mailTaskExecutor")
    public void notificarRegistroASuperAdmin(Usuario nuevoUsuario) {
        if (nuevoUsuario == null) return;

        String nombreNuevo = ((nuevoUsuario.getNombre() != null ? nuevoUsuario.getNombre() : "") + " " +
                (nuevoUsuario.getApellido() != null ? nuevoUsuario.getApellido() : "")).trim();
        if (nombreNuevo.isBlank()) nombreNuevo = nuevoUsuario.getEmail();

        // Generar notificación interna en el header para Super Admins y Administradores
        try {
            notificacionService.enviarATodosLosAdmins(
                "👤 Nuevo Registro de Usuario",
                "El usuario " + nombreNuevo + " (" + nuevoUsuario.getEmail() + ") se ha registrado en el sistema con el rol " + templateBuilder.getRoleDisplayName(nuevoUsuario.getRol()) + ".",
                com.proyecto.proyectoSpringBoot.model.enums.TipoNotificacion.INFO
            );
        } catch (Exception e) {
            log.error("[EmailService] Error generando notificación interna de nuevo registro para administradores: {}", e.getMessage());
        }

        if (!mailEnabled) return;

        java.util.List<Usuario> superAdmins = usuarioRepository.findByRol(RolUsuario.SUPER_ADMIN);
        if (superAdmins.isEmpty()) {
            superAdmins = usuarioRepository.findByRol(RolUsuario.ADMIN);
        }

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

    @Override
    @Async("mailTaskExecutor")
    public void notificarCambioRol(Usuario usuario, RolUsuario rolAnterior, RolUsuario nuevoRol) {
        if (usuario == null) return;

        // Generar notificación interna en el header del usuario afectado
        try {
            notificacionService.enviar(
                usuario.getId(),
                "🔰 Actualización de Rol",
                "Tu rol en la plataforma ha sido actualizado de " + templateBuilder.getRoleDisplayName(rolAnterior) + " a " + templateBuilder.getRoleDisplayName(nuevoRol) + ".",
                com.proyecto.proyectoSpringBoot.model.enums.TipoNotificacion.INFO
            );
        } catch (Exception e) {
            log.error("[EmailService] Error generando notificación interna de cambio de rol: {}", e.getMessage());
        }

        if (!mailEnabled || usuario.getEmail() == null || usuario.getEmail().isBlank() || mailSender == null) {
            return;
        }

        try {
            String nombreCompleto = ((usuario.getNombre() != null ? usuario.getNombre() : "") + " " +
                    (usuario.getApellido() != null ? usuario.getApellido() : "")).trim();
            if (nombreCompleto.isBlank()) nombreCompleto = usuario.getEmail();

            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, MimeMessageHelper.MULTIPART_MODE_MIXED_RELATED, StandardCharsets.UTF_8.name());

            String remitente = (smtpUsername != null && !smtpUsername.isBlank()) ? smtpUsername : mailFrom;
            helper.setFrom(remitente, "LogiTrack S.A. | Gestión de Usuarios");
            helper.setTo(usuario.getEmail());
            helper.setSubject("🔰 Actualización de Permisos: Tu rol ha sido cambiado a " + templateBuilder.getRoleDisplayName(nuevoRol));

            String htmlBody = templateBuilder.buildRoleChangeEmailHtml(nombreCompleto, usuario.getEmail(), rolAnterior, nuevoRol);
            helper.setText(htmlBody, true);

            mailSender.send(message);
            log.info("[EmailService] Notificación de cambio de rol enviada exitosamente a: {} ({} -> {})", usuario.getEmail(), rolAnterior, nuevoRol);
        } catch (Exception e) {
            log.error("[EmailService] Error enviando correo de cambio de rol a {}: {}", usuario.getEmail(), e.getMessage());
        }
    }

    @Override
    @Async("mailTaskExecutor")
    public void enviarReporteDiario(List<Usuario> destinatarios, com.proyecto.proyectoSpringBoot.dto.response.ReporteDiarioDTO resumen) {
        if (destinatarios == null || destinatarios.isEmpty()) return;

        // Generar notificación interna en el header para cada destinatario del reporte
        for (Usuario u : destinatarios) {
            try {
                notificacionService.enviar(
                    u.getId(),
                    "📊 Reporte Ejecutivo Diario Enviado",
                    "Se ha generado y enviado el reporte ejecutivo diario a tu correo electrónico (" + (resumen != null ? resumen.getTotalMovimientosHoy() : 0) + " movimientos registrados hoy).",
                    com.proyecto.proyectoSpringBoot.model.enums.TipoNotificacion.INFO
                );
            } catch (Exception e) {
                log.error("[EmailService] Error generando notificación interna de reporte diario para usuario {}: {}", u.getId(), e.getMessage());
            }
        }

        if (!mailEnabled || mailSender == null) return;

        for (Usuario u : destinatarios) {
            try {
                if (u.getEmail() == null || u.getEmail().isBlank()) continue;
                String nombreCompleto = ((u.getNombre() != null ? u.getNombre() : "") + " " +
                        (u.getApellido() != null ? u.getApellido() : "")).trim();
                if (nombreCompleto.isBlank()) nombreCompleto = u.getEmail();

                MimeMessage message = mailSender.createMimeMessage();
                MimeMessageHelper helper = new MimeMessageHelper(message, MimeMessageHelper.MULTIPART_MODE_MIXED_RELATED, StandardCharsets.UTF_8.name());

                String remitente = (smtpUsername != null && !smtpUsername.isBlank()) ? smtpUsername : mailFrom;
                helper.setFrom(remitente, "LogiTrack S.A. | Reporte Ejecutivo");
                helper.setTo(u.getEmail());
                helper.setSubject("📊 Reporte Ejecutivo Diario de Operaciones y Auditoría - LogiTrack S.A.");

                String htmlBody = templateBuilder.buildDailyReportEmailHtml(nombreCompleto, resumen);
                helper.setText(htmlBody, true);

                mailSender.send(message);
                log.info("[EmailService] Reporte diario enviado exitosamente a: {}", u.getEmail());
            } catch (Exception e) {
                log.error("[EmailService] Error enviando reporte diario a {}: {}", u.getEmail(), e.getMessage());
            }
        }
    }

    @Override
    @Async("mailTaskExecutor")
    public void notificarStockBajo(com.proyecto.proyectoSpringBoot.model.entity.Producto producto, com.proyecto.proyectoSpringBoot.model.entity.Bodega bodega, Integer stockActual) {
        if (!mailEnabled || mailSender == null || producto == null || bodega == null) {
            return;
        }

        List<Usuario> admins = usuarioRepository.findAll().stream()
                .filter(u -> u.isActivo())
                .filter(u -> u.getRol() == RolUsuario.SUPER_ADMIN || 
                             u.getRol() == RolUsuario.ADMIN || 
                             u.getRol() == RolUsuario.SUPERVISOR || 
                             u.getRol() == RolUsuario.GERENTE_LOGISTICA || 
                             u.getRol() == RolUsuario.JEFE_COMPRAS)
                .toList();

        for (Usuario u : admins) {
            try {
                if (u.getEmail() == null || u.getEmail().isBlank()) continue;

                MimeMessage message = mailSender.createMimeMessage();
                MimeMessageHelper helper = new MimeMessageHelper(message, MimeMessageHelper.MULTIPART_MODE_MIXED_RELATED, StandardCharsets.UTF_8.name());

                String remitente = (smtpUsername != null && !smtpUsername.isBlank()) ? smtpUsername : mailFrom;
                helper.setFrom(remitente, "LogiTrack S.A. | Alerta de Inventario");
                helper.setTo(u.getEmail());
                helper.setSubject("⚠️ Alerta Crítica de Stock Bajo: " + producto.getNombre() + " (" + stockActual + " / " + producto.getStockMinimo() + ")");

                String htmlBody = templateBuilder.buildLowStockEmailHtml(producto, bodega, stockActual);
                helper.setText(htmlBody, true);

                mailSender.send(message);
                log.info("[EmailService] Alerta de stock bajo enviada por correo a: {}", u.getEmail());
            } catch (Exception e) {
                log.error("[EmailService] Error enviando correo de stock bajo a {}: {}", u.getEmail(), e.getMessage());
            }
        }
    }
}
