package com.proyecto.proyectoSpringBoot.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class RechazarSolicitudRequest {
    @NotBlank
    @Size(max=500)
    private String motivoRechazo;
}
