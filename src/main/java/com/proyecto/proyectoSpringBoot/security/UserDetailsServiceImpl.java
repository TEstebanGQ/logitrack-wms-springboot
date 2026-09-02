package com.proyecto.proyectoSpringBoot.security;

import com.proyecto.proyectoSpringBoot.model.entity.Usuario;
import com.proyecto.proyectoSpringBoot.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UsuarioRepository usuarioRepository;

    @Override
    @Cacheable(value = "usuariosAuth", key = "#email", unless = "#result == null")
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Usuario u = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado: " + email));

        return CachedUserDetails.builder()
                .id(u.getId())
                .username(u.getEmail())
                .password(u.getPassword())
                .rol(u.getRol() != null ? u.getRol().name() : "EMPLEADO")
                .activo(u.isActivo())
                .build();
    }
}
