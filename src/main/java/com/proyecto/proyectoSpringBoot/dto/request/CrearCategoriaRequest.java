package com.proyecto.proyectoSpringBoot.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CrearCategoriaRequest {
    @NotBlank
    @Size(max=100)
    private String nombre;

    @Size(max=500)
    private String descripcion;
}
