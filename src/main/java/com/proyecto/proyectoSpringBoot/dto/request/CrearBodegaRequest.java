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
    @NotBlank(message = "El nombre de la bodega es obligatorio")
    @Size(min = 2, max = 150, message = "El nombre debe tener entre 2 y 150 caracteres")
    private String nombre;

    @NotBlank(message = "La ubicación es obligatoria")
    @Size(min = 3, max = 255, message = "La ubicación debe tener entre 3 y 255 caracteres")
    private String ubicacion;

    @Min(value = 1, message = "La capacidad debe ser al menos 1")
    private Integer capacidad;

    @NotBlank(message = "El encargado es obligatorio")
    @Size(min = 2, max = 150, message = "El nombre del encargado debe tener entre 2 y 150 caracteres")
    private String encargado;

    private Boolean activo;
}
