package com.proyecto.proyectoSpringBoot.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CrearClienteRequest {
    @NotBlank(message = "El nombre del cliente es obligatorio")
    @Size(min = 2, max = 150, message = "El nombre debe tener entre 2 y 150 caracteres")
    private String nombre;
    
    @Size(max = 50, message = "El RUC/NIT no puede exceder 50 caracteres")
    private String ruc;
    
    @Size(max = 20, message = "El teléfono no puede exceder 20 caracteres")
    private String telefono;
    
    @Email(message = "El formato de correo es inválido")
    @Size(max = 150, message = "El email no puede exceder 150 caracteres")
    private String email;
    
    @Size(max = 300, message = "La dirección no puede exceder 300 caracteres")
    private String direccion;
}
