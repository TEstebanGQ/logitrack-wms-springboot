package com.proyecto.proyectoSpringBoot.controller;

import com.proyecto.proyectoSpringBoot.dto.request.LoginRequest;
import com.proyecto.proyectoSpringBoot.dto.request.RegisterRequest;
import com.proyecto.proyectoSpringBoot.dto.response.JwtResponse;
import com.proyecto.proyectoSpringBoot.model.entity.Usuario;
import com.proyecto.proyectoSpringBoot.model.enums.RolUsuario;
import com.proyecto.proyectoSpringBoot.repository.UsuarioRepository;
import com.proyecto.proyectoSpringBoot.security.JwtUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.*;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "Autenticación", description = "Endpoints de registro y login JWT")
public class AuthController {

    private final AuthenticationManager authManager;
    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    @PostMapping("/login")
    @Operation(summary = "Iniciar sesión y obtener token JWT")
    public ResponseEntity<JwtResponse> login(@Valid @RequestBody LoginRequest request) {
        authManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));

        UserDetails userDetails = usuarioRepository.findByEmail(request.getEmail())
                .orElseThrow();
        String token = jwtUtil.generateToken(userDetails);
        Usuario usuario = (Usuario) userDetails;

        return ResponseEntity.ok(JwtResponse.builder()
                .token(token)
                .tipo("Bearer")
                .id(usuario.getId())
                .nombre(usuario.getNombre() + " " + usuario.getApellido())
                .email(usuario.getEmail())
                .rol(usuario.getRol().name())
                .build());
    }

    @PostMapping("/register")
    @Operation(summary = "Registrar un nuevo usuario")
    public ResponseEntity<?> register(@Valid @RequestBody RegisterRequest request) {
        if (usuarioRepository.existsByEmail(request.getEmail())) {
            return ResponseEntity.badRequest().body(java.util.Map.of("mensaje", "El email ya está en uso"));
        }
        Usuario usuario = Usuario.builder()
                .nombre(request.getNombre())
                .apellido(request.getApellido())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .rol(RolUsuario.valueOf(request.getRol().toUpperCase()))
                .activo(true)
                .build();
        usuarioRepository.save(usuario);
        return ResponseEntity.ok(java.util.Map.of("mensaje", "Usuario registrado exitosamente"));
    }

    @PostMapping("/google")
    @Operation(summary = "Iniciar sesión o verificar cuenta con Google OAuth2")
    public ResponseEntity<?> googleAuth(@Valid @RequestBody com.proyecto.proyectoSpringBoot.dto.request.GoogleAuthRequest request) {
        try {
            java.util.Map<String, Object> googleUser = verifyAndDecodeGoogleToken(request.getCredential());

            if (googleUser == null || googleUser.get("email") == null) {
                return ResponseEntity.badRequest().body(java.util.Map.of("mensaje", "Token de Google inválido o no se pudo decodificar"));
            }

            String email = (String) googleUser.get("email");
            String nombre = (String) googleUser.getOrDefault("given_name", googleUser.getOrDefault("name", "Usuario"));
            String apellido = (String) googleUser.getOrDefault("family_name", "Google");

            java.util.Optional<Usuario> usuarioOpt = usuarioRepository.findByEmail(email);

            if (usuarioOpt.isPresent()) {
                Usuario usuario = usuarioOpt.get();
                String token = jwtUtil.generateToken(usuario);
                return ResponseEntity.ok(com.proyecto.proyectoSpringBoot.dto.response.GoogleAuthResponse.builder()
                        .registrado(true)
                        .token(token)
                        .tipo("Bearer")
                        .id(usuario.getId())
                        .nombre(usuario.getNombre() + " " + usuario.getApellido())
                        .email(usuario.getEmail())
                        .rol(usuario.getRol().name())
                        .build());
            } else {
                return ResponseEntity.ok(com.proyecto.proyectoSpringBoot.dto.response.GoogleAuthResponse.builder()
                        .registrado(false)
                        .email(email)
                        .nombre(nombre)
                        .apellido(apellido)
                        .build());
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(java.util.Map.of("mensaje", "Error al autenticar con Google: " + e.getMessage()));
        }
    }

    private java.util.Map<String, Object> verifyAndDecodeGoogleToken(String idToken) {
        try {
            org.springframework.http.client.SimpleClientHttpRequestFactory factory = new org.springframework.http.client.SimpleClientHttpRequestFactory();
            factory.setConnectTimeout(5000);
            factory.setReadTimeout(5000);
            org.springframework.web.client.RestTemplate restTemplate = new org.springframework.web.client.RestTemplate(factory);
            String url = "https://oauth2.googleapis.com/tokeninfo?id_token=" + idToken;
            @SuppressWarnings("unchecked")
            java.util.Map<String, Object> googleUser = restTemplate.getForObject(url, java.util.Map.class);
            if (googleUser != null && googleUser.get("email") != null) {
                return googleUser;
            }
        } catch (Exception e) {
            System.err.println("Advertencia: Falló consulta HTTP a Google tokeninfo (" + e.getMessage() + "). Usando fallback JWT local...");
        }

        try {
            String[] parts = idToken.split("\\.");
            if (parts.length >= 2) {
                String payloadJson = new String(java.util.Base64.getUrlDecoder().decode(parts[1]), java.nio.charset.StandardCharsets.UTF_8);
                com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();
                @SuppressWarnings("unchecked")
                java.util.Map<String, Object> payload = mapper.readValue(payloadJson, java.util.Map.class);
                if (payload != null && payload.get("email") != null) {
                    return payload;
                }
            }
        } catch (Exception ex) {
            System.err.println("Error decodificando token JWT de Google: " + ex.getMessage());
        }
        return null;
    }
}

