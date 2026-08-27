package com.proyecto.proyectoSpringBoot.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CrearBodegaRequest {
    @NotBlank @Size(max = 150)
    private String nombre;
    @NotBlank @Size(max = 255)
    private String ubicacion;
    @Min(1)
    private Integer capacidad;
    @NotBlank @Size(max = 150)
    private String encargado;
    private Boolean activo;
}
