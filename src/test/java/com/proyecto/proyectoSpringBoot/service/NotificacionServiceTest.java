package com.proyecto.proyectoSpringBoot.service;

import com.proyecto.proyectoSpringBoot.dto.response.ContadorNotificacionesResponse;
import com.proyecto.proyectoSpringBoot.dto.response.NotificacionResponse;
import com.proyecto.proyectoSpringBoot.exception.ResourceNotFoundException;
import com.proyecto.proyectoSpringBoot.model.entity.Usuario;
import com.proyecto.proyectoSpringBoot.model.enums.TipoNotificacion;
import com.proyecto.proyectoSpringBoot.repository.UsuarioRepository;
import com.proyecto.proyectoSpringBoot.service.interfaces.INotificacionService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class NotificacionServiceTest {

    @Autowired
    private INotificacionService notificacionService;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Test
    @DisplayName("01. Debe enviar notificación a un usuario específico")
    void testEnviarNotificacion() {
        Usuario admin = usuarioRepository.findByEmail("admin@logitrack.com").orElseThrow();
        notificacionService.enviar(admin.getId(), "Prueba Alerta", "Mensaje de prueba unitaria", TipoNotificacion.INFO);

        List<NotificacionResponse> notifs = notificacionService.misNotificaciones("admin@logitrack.com");
        assertFalse(notifs.isEmpty());
        assertTrue(notifs.stream().anyMatch(n -> n.getTitulo().equals("Prueba Alerta")));
    }

    @Test
    @DisplayName("02. Debe enviar notificación masiva a todos los administradores")
    void testEnviarATodosLosAdmins() {
        notificacionService.enviarATodosLosAdmins("Alerta General", "Aviso global para administradores", TipoNotificacion.ALERTA);

        List<NotificacionResponse> notifs = notificacionService.misNotificaciones("admin@logitrack.com");
        assertTrue(notifs.stream().anyMatch(n -> n.getTitulo().equals("Alerta General")));
    }

    @Test
    @DisplayName("03. Debe contar correctamente las notificaciones no leídas")
    void testContarNoLeidas() {
        Usuario admin = usuarioRepository.findByEmail("admin@logitrack.com").orElseThrow();
        ContadorNotificacionesResponse antes = notificacionService.contarNoLeidas("admin@logitrack.com");

        notificacionService.enviar(admin.getId(), "Nueva no leída", "Test contador", TipoNotificacion.INFO);

        ContadorNotificacionesResponse despues = notificacionService.contarNoLeidas("admin@logitrack.com");
        assertEquals(antes.getNoLeidas() + 1, despues.getNoLeidas());
    }

    @Test
    @DisplayName("04. Debe marcar una notificación como leída")
    void testMarcarLeida() {
        Usuario admin = usuarioRepository.findByEmail("admin@logitrack.com").orElseThrow();
        notificacionService.enviar(admin.getId(), "Para marcar leída", "Test lectura", TipoNotificacion.EXITO);

        List<NotificacionResponse> notifs = notificacionService.misNotificaciones("admin@logitrack.com");
        NotificacionResponse nueva = notifs.stream().filter(n -> n.getTitulo().equals("Para marcar leída")).findFirst().orElseThrow();
        assertFalse(nueva.isLeida());

        notificacionService.marcarLeida(nueva.getId(), "admin@logitrack.com");

        List<NotificacionResponse> notifsPost = notificacionService.misNotificaciones("admin@logitrack.com");
        NotificacionResponse leida = notifsPost.stream().filter(n -> n.getId().equals(nueva.getId())).findFirst().orElseThrow();
        assertTrue(leida.isLeida());
    }

    @Test
    @DisplayName("05. Debe marcar todas las notificaciones como leídas")
    void testMarcarTodasLeidas() {
        Usuario admin = usuarioRepository.findByEmail("admin@logitrack.com").orElseThrow();
        notificacionService.enviar(admin.getId(), "No leída 1", "Mensaje 1", TipoNotificacion.INFO);
        notificacionService.enviar(admin.getId(), "No leída 2", "Mensaje 2", TipoNotificacion.ALERTA);

        notificacionService.marcarTodasLeidas("admin@logitrack.com");

        ContadorNotificacionesResponse res = notificacionService.contarNoLeidas("admin@logitrack.com");
        assertEquals(0, res.getNoLeidas());
    }

    @Test
    @DisplayName("06. Debe lanzar excepción al buscar notificaciones de usuario inexistente")
    void testUsuarioInexistente() {
        assertThrows(ResourceNotFoundException.class, () -> notificacionService.misNotificaciones("noexiste@logitrack.com"));
    }

    @Test
    @DisplayName("07. Debe ignorar envío si el ID de usuario no existe")
    void testEnviarUsuarioInexistente() {
        assertDoesNotThrow(() -> notificacionService.enviar(99999L, "Test", "Msg", TipoNotificacion.INFO));
    }
}
