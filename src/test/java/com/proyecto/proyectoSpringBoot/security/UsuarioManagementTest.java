package com.proyecto.proyectoSpringBoot.security;

import com.proyecto.proyectoSpringBoot.model.entity.Usuario;
import com.proyecto.proyectoSpringBoot.model.enums.RolUsuario;
import com.proyecto.proyectoSpringBoot.repository.UsuarioRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class UsuarioManagementTest {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Test
    @DisplayName("01. Debe encontrar usuario admin predeterminado por email")
    void testBuscarAdminPorEmail() {
        Optional<Usuario> admin = usuarioRepository.findByEmail("admin@logitrack.com");
        assertTrue(admin.isPresent());
        assertEquals(RolUsuario.ADMIN, admin.get().getRol());
        assertTrue(admin.get().isActivo());
    }

    @Test
    @DisplayName("02. Debe verificar correctamente existencia de email")
    void testExistsByEmail() {
        assertTrue(usuarioRepository.existsByEmail("admin@logitrack.com"));
        assertFalse(usuarioRepository.existsByEmail("noexiste_123@logitrack.com"));
    }

    @Test
    @DisplayName("03. Debe crear y codificar contraseña de nuevo usuario")
    void testCrearUsuarioConPasswordEncriptado() {
        Usuario nuevo = Usuario.builder()
                .nombre("Laura")
                .apellido("Martínez")
                .email("laura.martinez@logitrack.com")
                .password(passwordEncoder.encode("clave123"))
                .rol(RolUsuario.SUPERVISOR)
                .activo(true)
                .build();

        Usuario guardado = usuarioRepository.save(nuevo);
        assertNotNull(guardado.getId());
        assertTrue(passwordEncoder.matches("clave123", guardado.getPassword()));
        assertEquals(RolUsuario.SUPERVISOR, guardado.getRol());
    }

    @Test
    @DisplayName("04. Debe filtrar usuarios por rol")
    void testFindByRol() {
        List<Usuario> admins = usuarioRepository.findByRol(RolUsuario.ADMIN);
        assertFalse(admins.isEmpty());
        assertTrue(admins.stream().allMatch(u -> u.getRol() == RolUsuario.ADMIN));
    }

    @Test
    @DisplayName("05. Debe desactivar usuario correctamente")
    void testDesactivarUsuario() {
        Usuario nuevo = Usuario.builder()
                .nombre("Temporal")
                .apellido("User")
                .email("temp@logitrack.com")
                .password(passwordEncoder.encode("pass"))
                .rol(RolUsuario.EMPLEADO)
                .activo(true)
                .build();
        Usuario guardado = usuarioRepository.save(nuevo);

        guardado.setActivo(false);
        usuarioRepository.save(guardado);

        Usuario recuperado = usuarioRepository.findById(guardado.getId()).orElseThrow();
        assertFalse(recuperado.isActivo());
    }
}
