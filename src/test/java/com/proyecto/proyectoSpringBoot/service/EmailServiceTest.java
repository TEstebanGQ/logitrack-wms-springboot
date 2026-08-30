package com.proyecto.proyectoSpringBoot.service;

import com.proyecto.proyectoSpringBoot.model.entity.Usuario;
import com.proyecto.proyectoSpringBoot.model.enums.RolUsuario;
import com.proyecto.proyectoSpringBoot.service.impl.EmailServiceImpl;
import com.proyecto.proyectoSpringBoot.service.impl.EmailTemplateBuilder;
import jakarta.mail.Session;
import jakarta.mail.internet.MimeMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.mail.MailSendException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EmailServiceTest {

    @Mock
    private JavaMailSender mailSender;

    @Mock
    private ObjectProvider<JavaMailSender> mailSenderProvider;

    private EmailTemplateBuilder templateBuilder;
    private EmailServiceImpl emailService;

    @BeforeEach
    void setUp() {
        templateBuilder = new EmailTemplateBuilder();
        when(mailSenderProvider.getIfAvailable()).thenReturn(mailSender);
        emailService = new EmailServiceImpl(mailSenderProvider, templateBuilder);
        ReflectionTestUtils.setField(emailService, "mailEnabled", true);
        ReflectionTestUtils.setField(emailService, "mailFrom", "no-reply@logitrack.com");
        ReflectionTestUtils.setField(emailService, "smtpUsername", "soporte@logitrack.com");
    }

    @Test
    @DisplayName("01. Debe construir plantilla HTML corporativa para Rol ADMIN")
    void testPlantillaAdmin() {
        String html = templateBuilder.buildWelcomeEmailHtml("Carlos Administrador", "carlos.admin@logitrack.com", RolUsuario.ADMIN);
        String subject = templateBuilder.buildWelcomeSubject(RolUsuario.ADMIN);

        assertNotNull(html);
        assertTrue(html.contains("Carlos Administrador"));
        assertTrue(html.contains("carlos.admin@logitrack.com"));
        assertTrue(html.contains("Administrador del Sistema"));
        assertTrue(html.contains("Gestión de Usuarios &amp; Seguridad") || html.contains("Gestión de Usuarios & Seguridad"));
        assertTrue(html.contains("Auditoría Global"));
        assertTrue(subject.contains("Administrador del Sistema"));
    }

    @Test
    @DisplayName("02. Debe construir plantilla HTML corporativa para Rol SUPERVISOR")
    void testPlantillaSupervisor() {
        String html = templateBuilder.buildWelcomeEmailHtml("Laura Pérez", "laura.supervisor@logitrack.com", RolUsuario.SUPERVISOR);
        String subject = templateBuilder.buildWelcomeSubject(RolUsuario.SUPERVISOR);

        assertNotNull(html);
        assertTrue(html.contains("Laura Pérez"));
        assertTrue(html.contains("Supervisor de Operaciones &amp; Calidad") || html.contains("Supervisor de Operaciones & Calidad"));
        assertTrue(html.contains("Conteos Cíclicos"));
        assertTrue(html.contains("Supervisión de Picking"));
        assertTrue(subject.contains("Supervisor de Operaciones"));
    }

    @Test
    @DisplayName("03. Debe construir plantilla HTML corporativa para Rol GERENTE_LOGISTICA")
    void testPlantillaGerenteLogistica() {
        String html = templateBuilder.buildWelcomeEmailHtml("Sofía Ramírez", "sofia.gerente@logitrack.com", RolUsuario.GERENTE_LOGISTICA);
        String subject = templateBuilder.buildWelcomeSubject(RolUsuario.GERENTE_LOGISTICA);

        assertNotNull(html);
        assertTrue(html.contains("Sofía Ramírez"));
        assertTrue(html.contains("Gerente de Logística &amp; Distribución") || html.contains("Gerente de Logística & Distribución"));
        assertTrue(html.contains("Guías de Despacho"));
        assertTrue(html.contains("Estrategia &amp; KPIs") || html.contains("Estrategia & KPIs"));
        assertTrue(subject.contains("Gerencia Logística"));
    }

    @Test
    @DisplayName("04. Debe construir plantilla HTML corporativa para Rol JEFE_COMPRAS")
    void testPlantillaJefeCompras() {
        String html = templateBuilder.buildWelcomeEmailHtml("Pedro Sánchez", "pedro.compras@logitrack.com", RolUsuario.JEFE_COMPRAS);
        String subject = templateBuilder.buildWelcomeSubject(RolUsuario.JEFE_COMPRAS);

        assertNotNull(html);
        assertTrue(html.contains("Pedro Sánchez"));
        assertTrue(html.contains("Jefe de Compras &amp; Abastecimiento") || html.contains("Jefe de Compras & Abastecimiento"));
        assertTrue(html.contains("Órdenes de Compra"));
        assertTrue(html.contains("Directorio de Proveedores"));
        assertTrue(subject.contains("Jefatura de Compras"));
    }

    @Test
    @DisplayName("05. Debe construir plantilla HTML corporativa para Rol EMPLEADO")
    void testPlantillaEmpleado() {
        String html = templateBuilder.buildWelcomeEmailHtml("Andrés Operario", "andres.operario@logitrack.com", RolUsuario.EMPLEADO);
        String subject = templateBuilder.buildWelcomeSubject(RolUsuario.EMPLEADO);

        assertNotNull(html);
        assertTrue(html.contains("Andrés Operario"));
        assertTrue(html.contains("Especialista en Operaciones &amp; Bodega") || html.contains("Especialista en Operaciones & Bodega"));
        assertTrue(html.contains("Picking Asistido"));
        assertTrue(html.contains("Trazabilidad Unitaria"));
        assertTrue(subject.contains("Especialista de Almacén"));
    }

    @Test
    @DisplayName("06. Debe enviar el correo correctamente llamando a JavaMailSender")
    void testEnviarCorreoBienvenidaExitoso() {
        Usuario usuario = Usuario.builder()
                .id(10L)
                .nombre("Mario")
                .apellido("Bross")
                .email("mario@logitrack.com")
                .rol(RolUsuario.ADMIN)
                .build();

        MimeMessage mimeMessage = new MimeMessage((Session) null);
        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);

        assertDoesNotThrow(() -> emailService.enviarCorreoBienvenida(usuario));
        verify(mailSender, times(1)).send(any(MimeMessage.class));
    }

    @Test
    @DisplayName("07. No debe lanzar excepción si falla la conexión SMTP con Google")
    void testEnvioFallaSinInterrumpirFlujo() {
        Usuario usuario = Usuario.builder()
                .id(11L)
                .nombre("Luigi")
                .apellido("Verde")
                .email("luigi@logitrack.com")
                .rol(RolUsuario.EMPLEADO)
                .build();

        MimeMessage mimeMessage = new MimeMessage((Session) null);
        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);
        doThrow(new MailSendException("Error de autenticación SMTP 535"))
                .when(mailSender).send(any(MimeMessage.class));

        // El método debe capturar el error y no propagar excepción
        assertDoesNotThrow(() -> emailService.enviarCorreoBienvenida(usuario));
        verify(mailSender, times(1)).send(any(MimeMessage.class));
    }

    @Test
    @DisplayName("08. Debe ignorar el envío si el correo o usuario es nulo")
    void testUsuarioNuloOInvalido() {
        assertDoesNotThrow(() -> emailService.enviarCorreoBienvenida(null));
        Usuario sinEmail = Usuario.builder().nombre("Sin").apellido("Email").build();
        assertDoesNotThrow(() -> emailService.enviarCorreoBienvenida(sinEmail));

        verify(mailSender, never()).send(any(MimeMessage.class));
    }
}
