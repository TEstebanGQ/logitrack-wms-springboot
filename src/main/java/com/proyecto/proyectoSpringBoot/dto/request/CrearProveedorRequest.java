package com.proyecto.proyectoSpringBoot.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CrearProveedorRequest {
    @NotBlank @Size(max=150)
    private String nombre;
    
    @Size(max=50)
    private String ruc;
    
    @Size(max=20)
    private String telefono;
    
    @Email @Size(max=150)
    private String email;
    
    @Size(max=300)
    private String direccion;
}
