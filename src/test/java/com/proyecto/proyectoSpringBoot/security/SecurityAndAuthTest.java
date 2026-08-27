package com.proyecto.proyectoSpringBoot.security;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class SecurityAndAuthTest {

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Test
    @DisplayName("32. Debe verificar la codificación y coincidencia de contraseñas con BCrypt")
    void testBCryptPasswordEncoding() {
        String passRaw = "admin123";
        String encoded = passwordEncoder.encode(passRaw);

        assertNotNull(encoded);
        assertNotEquals(passRaw, encoded);
        assertTrue(passwordEncoder.matches(passRaw, encoded));
    }

    @Test
    @DisplayName("33. Debe generar un Token JWT válido para un usuario")
    void testGenerarTokenJwt() {
        UserDetails user = new User("admin@logitrack.com", "pass", List.of(new SimpleGrantedAuthority("ROLE_ADMIN")));
        String token = jwtUtil.generateToken(user);

        assertNotNull(token);
        assertFalse(token.isBlank());
    }

    @Test
    @DisplayName("34. Debe extraer correctamente el username del Token JWT")
    void testExtraerUsernameTokenJwt() {
        UserDetails user = new User("carlos@logitrack.com", "pass", List.of(new SimpleGrantedAuthority("ROLE_EMPLEADO")));
        String token = jwtUtil.generateToken(user);

        String username = jwtUtil.extractUsername(token);
        assertEquals("carlos@logitrack.com", username);
    }

    @Test
    @DisplayName("35. Debe validar correctamente la vigencia del Token JWT")
    void testValidarTokenJwt() {
        UserDetails user = new User("maria@logitrack.com", "pass", List.of(new SimpleGrantedAuthority("ROLE_EMPLEADO")));
        String token = jwtUtil.generateToken(user);

        assertTrue(jwtUtil.validateToken(token, user));
    }
}
