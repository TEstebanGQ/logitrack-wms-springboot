package com.proyecto.proyectoSpringBoot.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
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
    private String codigoUbicacion;

    @NotBlank(message = "El pasillo es obligatorio")
    private String pasillo;

    @NotBlank(message = "El estante/rack es obligatorio")
    private String estante;

    @NotBlank(message = "El nivel o altura es obligatorio")
    private String nivel;

    @Positive(message = "La capacidad máxima debe ser mayor a cero")
    private Integer capacidadMax;

    private String descripcion;
}
