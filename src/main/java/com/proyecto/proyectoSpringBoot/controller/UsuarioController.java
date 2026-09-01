package com.proyecto.proyectoSpringBoot.controller;

import com.proyecto.proyectoSpringBoot.dto.request.UsuarioRequest;
import com.proyecto.proyectoSpringBoot.dto.response.UsuarioResponse;
import com.proyecto.proyectoSpringBoot.model.entity.Usuario;
import com.proyecto.proyectoSpringBoot.model.enums.RolUsuario;
import com.proyecto.proyectoSpringBoot.repository.UsuarioRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/usuarios")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Usuarios", description = "Gestión de usuarios (Solo Admin)")
@PreAuthorize("hasRole('ADMIN')")
public class UsuarioController {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    @GetMapping
    @Operation(summary = "Listar todos los usuarios")
    public ResponseEntity<List<UsuarioResponse>> listar() {
        List<UsuarioResponse> list = usuarioRepository.findAll().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(list);
    }

    @PostMapping
    @Operation(summary = "Crear nuevo usuario")
    public ResponseEntity<?> crear(@Valid @RequestBody UsuarioRequest request) {
        if (usuarioRepository.existsByEmail(request.getEmail())) {
            return ResponseEntity.badRequest().body(java.util.Map.of("mensaje", "El email ya está en uso"));
        }
        if (request.getPassword() == null || request.getPassword().isBlank()) {
            return ResponseEntity.badRequest().body(java.util.Map.of("mensaje", "La contraseña es obligatoria para nuevos usuarios"));
        }

        RolUsuario rolAsignado = request.getRol() != null ? RolUsuario.valueOf(request.getRol().toUpperCase()) : RolUsuario.EMPLEADO;
        if (rolAsignado == RolUsuario.SUPER_ADMIN && !esSuperAdminActual()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(java.util.Map.of("mensaje", "Acceso denegado: Únicamente un Super Administrador (SUPER_ADMIN) puede asignar el rol SUPER_ADMIN."));
        }

        Usuario u = Usuario.builder()
                .nombre(request.getNombre())
                .apellido(request.getApellido())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .rol(rolAsignado)
                .activo(request.getActivo() != null ? request.getActivo() : true)
                .build();
        Usuario guardado = usuarioRepository.save(u);
        return ResponseEntity.status(HttpStatus.CREATED).body(toResponse(guardado));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar un usuario existente")
    public ResponseEntity<?> actualizar(@PathVariable Long id, @RequestBody UsuarioRequest request) {
        Usuario u = usuarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        if (request.getNombre() != null) u.setNombre(request.getNombre());
        if (request.getApellido() != null) u.setApellido(request.getApellido());
        if (request.getEmail() != null && !request.getEmail().equals(u.getEmail())) {
            if (usuarioRepository.existsByEmail(request.getEmail())) {
                return ResponseEntity.badRequest().body(java.util.Map.of("mensaje", "El email ya está en uso"));
            }
            u.setEmail(request.getEmail());
        }
        if (request.getPassword() != null && !request.getPassword().isBlank()) {
            u.setPassword(passwordEncoder.encode(request.getPassword()));
        }
        if (request.getRol() != null) {
            RolUsuario nuevoRol = RolUsuario.valueOf(request.getRol().toUpperCase());
            if ((nuevoRol == RolUsuario.SUPER_ADMIN || u.getRol() == RolUsuario.SUPER_ADMIN) && !esSuperAdminActual()) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(java.util.Map.of("mensaje", "Acceso denegado: Únicamente un Super Administrador (SUPER_ADMIN) puede otorgar o modificar el rol SUPER_ADMIN."));
            }
            u.setRol(nuevoRol);
        }
        if (request.getActivo() != null) {
            if (u.getRol() == RolUsuario.SUPER_ADMIN && !esSuperAdminActual()) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(java.util.Map.of("mensaje", "Acceso denegado: Únicamente un Super Administrador puede modificar el estado de otro Super Administrador."));
            }
            u.setActivo(request.getActivo());
        }

        Usuario guardado = usuarioRepository.save(u);
        return ResponseEntity.ok(toResponse(guardado));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar o desactivar usuario")
    public ResponseEntity<?> eliminar(@PathVariable Long id) {
        Usuario u = usuarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        if (u.getRol() == RolUsuario.SUPER_ADMIN && !esSuperAdminActual()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(java.util.Map.of("mensaje", "Acceso denegado: Únicamente un Super Administrador puede eliminar a un Super Administrador."));
        }

        try {
            usuarioRepository.delete(u);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            u.setActivo(false);
            usuarioRepository.save(u);
            return ResponseEntity.ok(java.util.Map.of("mensaje", "El usuario posee registros asociados (movimientos, etc.). Se ha desactivado en su lugar."));
        }
    }

    private boolean esSuperAdminActual() {
        var auth = org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication();
        if (auth == null) return false;
        return auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_SUPER_ADMIN"));
    }

    private UsuarioResponse toResponse(Usuario u) {
        return UsuarioResponse.builder()
                .id(u.getId())
                .nombre(u.getNombre())
                .apellido(u.getApellido())
                .email(u.getEmail())
                .rol(u.getRol())
                .activo(u.isActivo())
                .createdAt(u.getCreatedAt())
                .build();
    }
}
