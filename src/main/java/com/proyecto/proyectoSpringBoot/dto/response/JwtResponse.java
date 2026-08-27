package com.proyecto.proyectoSpringBoot.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class JwtResponse {
    private String token;
    @Builder.Default
    private String tipo = "Bearer";
    private Long id;
    private String nombre;
    private String email;
    private String rol;
}
