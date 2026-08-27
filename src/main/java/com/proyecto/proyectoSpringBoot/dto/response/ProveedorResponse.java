package com.proyecto.proyectoSpringBoot.dto.response;
import lombok.Data;
@Data
public class ProveedorResponse {
    private Long id;
    private String nombre;
    private String ruc;
    private String telefono;
    private String email;
    private String direccion;
    private boolean activo;
}
