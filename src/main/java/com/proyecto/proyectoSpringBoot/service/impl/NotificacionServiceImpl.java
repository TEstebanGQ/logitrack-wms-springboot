package com.proyecto.proyectoSpringBoot.service.impl;

import com.proyecto.proyectoSpringBoot.dto.response.ContadorNotificacionesResponse;
import com.proyecto.proyectoSpringBoot.dto.response.NotificacionResponse;
import com.proyecto.proyectoSpringBoot.exception.ResourceNotFoundException;
import com.proyecto.proyectoSpringBoot.mapper.NotificacionMapper;
import com.proyecto.proyectoSpringBoot.model.entity.Notificacion;
import com.proyecto.proyectoSpringBoot.model.entity.Usuario;
import com.proyecto.proyectoSpringBoot.model.enums.RolUsuario;
import com.proyecto.proyectoSpringBoot.model.enums.TipoNotificacion;
import com.proyecto.proyectoSpringBoot.repository.NotificacionRepository;
import com.proyecto.proyectoSpringBoot.repository.UsuarioRepository;
import com.proyecto.proyectoSpringBoot.service.interfaces.INotificacionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class NotificacionServiceImpl implements INotificacionService {
    private final NotificacionRepository repository;
    private final NotificacionMapper mapper;
    private final UsuarioRepository usuarioRepository;

    @Override
    @Transactional
    public void enviar(Long usuarioId, String titulo, String mensaje, TipoNotificacion tipo) {
        Usuario usuario = usuarioRepository.findById(usuarioId).orElse(null);
        if (usuario != null) {
            Notificacion n = Notificacion.builder()
                    .usuario(usuario)
                    .titulo(titulo)
                    .mensaje(mensaje)
                    .tipo(tipo)
                    .leida(false)
                    .build();
            repository.save(n);
        }
    }

    @Override
    @Transactional
    public void enviarATodosLosAdmins(String titulo, String mensaje, TipoNotificacion tipo) {
        List<Usuario> destinatarios = usuarioRepository.findAll().stream()
                .filter(u -> u.getRol() == RolUsuario.ADMIN || u.getRol() == RolUsuario.SUPERVISOR || u.getRol() == RolUsuario.GERENTE_LOGISTICA)
                .collect(Collectors.toList());
        for (Usuario u : destinatarios) {
            enviar(u.getId(), titulo, mensaje, tipo);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<NotificacionResponse> misNotificaciones(String emailUsuario) {
        Usuario usuario = usuarioRepository.findByEmail(emailUsuario).orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));
        return repository.findByUsuarioIdOrderByFechaCreacionDesc(usuario.getId()).stream().map(mapper::toResponse).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public ContadorNotificacionesResponse contarNoLeidas(String emailUsuario) {
        Usuario usuario = usuarioRepository.findByEmail(emailUsuario).orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));
        return new ContadorNotificacionesResponse(repository.countByUsuarioIdAndLeidaFalse(usuario.getId()));
    }

    @Override
    @Transactional
    public void marcarLeida(Long id, String emailUsuario) {
        Usuario usuario = usuarioRepository.findByEmail(emailUsuario).orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));
        Notificacion n = repository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Notificacion no encontrada"));
        if (n.getUsuario().getId().equals(usuario.getId())) {
            n.setLeida(true);
            n.setFechaLectura(LocalDateTime.now());
            repository.save(n);
        }
    }

    @Override
    @Transactional
    public void marcarTodasLeidas(String emailUsuario) {
        Usuario usuario = usuarioRepository.findByEmail(emailUsuario).orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));
        List<Notificacion> noLeidas = repository.findByUsuarioIdAndLeidaFalse(usuario.getId());
        noLeidas.forEach(n -> {
            n.setLeida(true);
            n.setFechaLectura(LocalDateTime.now());
        });
        repository.saveAll(noLeidas);
    }

    @Override
    @Transactional
    public void limpiarNotificacionesObsoletas(String nombreProducto, String nombreBodega) {
        String snippetProd = "producto " + nombreProducto;
        String snippetBodega = "bodega " + nombreBodega;
        List<Notificacion> notifs = repository.findAll().stream()
                .filter(n -> !n.isLeida() && n.getMensaje() != null && n.getMensaje().contains(snippetProd) && n.getMensaje().contains(snippetBodega))
                .collect(Collectors.toList());
        for (Notificacion n : notifs) {
            n.setLeida(true);
            n.setFechaLectura(LocalDateTime.now());
        }
        repository.saveAll(notifs);
    }
}
