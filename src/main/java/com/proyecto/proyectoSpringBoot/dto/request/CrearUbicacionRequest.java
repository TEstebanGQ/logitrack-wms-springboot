package com.proyecto.proyectoSpringBoot.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CrearUbicacionRequest {

    @NotNull(message = "La bodega es obligatoria")
    private Long bodegaId;

    @NotBlank(message = "El código de ubicación es obligatorio (ej. PAS-01-RACK-A)")
    @Size(min = 2, max = 50, message = "El código de ubicación debe tener entre 2 y 50 caracteres")
    private String codigoUbicacion;

    @NotBlank(message = "El pasillo es obligatorio")
    @Size(min = 1, max = 50, message = "El pasillo debe tener entre 1 y 50 caracteres")
    private String pasillo;

    @NotBlank(message = "El estante/rack es obligatorio")
    @Size(min = 1, max = 50, message = "El estante debe tener entre 1 y 50 caracteres")
    private String estante;

    @NotBlank(message = "El nivel o altura es obligatorio")
    @Size(min = 1, max = 50, message = "El nivel debe tener entre 1 y 50 caracteres")
    private String nivel;

    @Positive(message = "La capacidad máxima debe ser mayor a cero")
    private Integer capacidadMax;

    @Size(max = 255, message = "La descripción no puede exceder 255 caracteres")
    private String descripcion;
}
