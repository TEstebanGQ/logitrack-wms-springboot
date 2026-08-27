package com.proyecto.proyectoSpringBoot.dto.response;
import lombok.Data;
@Data
public class ConfiguracionResponse {
    private Long id;
    private String clave;
    private String valor;
    private String descripcion;
}
