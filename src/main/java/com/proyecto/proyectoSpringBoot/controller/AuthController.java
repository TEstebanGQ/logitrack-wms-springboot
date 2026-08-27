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
}
