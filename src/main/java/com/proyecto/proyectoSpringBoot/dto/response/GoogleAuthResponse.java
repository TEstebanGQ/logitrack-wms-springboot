package com.proyecto.proyectoSpringBoot.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GoogleAuthResponse {

    private boolean registrado;
    private String email;
    private String nombre;
    private String apellido;
    private String token;
    private String tipo;
    private Long id;
    private String rol;
}
