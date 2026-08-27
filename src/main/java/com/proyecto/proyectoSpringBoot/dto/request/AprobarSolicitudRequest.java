package com.proyecto.proyectoSpringBoot.dto.request;

import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class AprobarSolicitudRequest {
    @Size(max=500)
    private String observaciones;
}
