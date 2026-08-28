package com.proyecto.proyectoSpringBoot.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class TipoUbicacionRequest {
    @NotBlank(message = "El código es obligatorio")
    private String codigo;

    @NotBlank(message = "El nombre es obligatorio")
    private String nombre;

    @NotNull(message = "El peso máximo es obligatorio")
    @Positive(message = "El peso máximo debe ser positivo")
    private Double pesoMaximoKg;

    @NotNull(message = "El volumen máximo es obligatorio")
    @Positive(message = "El volumen máximo debe ser positivo")
    private Double volumenMaximoM3;

    private String descripcion;
    private Boolean activo;
}
