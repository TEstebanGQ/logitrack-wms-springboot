package com.proyecto.proyectoSpringBoot.dto.request;

import com.proyecto.proyectoSpringBoot.model.enums.TipoServicioTransporte;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class TransportadoraRequest {
    @NotBlank(message = "El nombre es obligatorio")
    @Size(min = 2, max = 150, message = "El nombre debe tener entre 2 y 150 caracteres")
    private String nombre;

    @NotBlank(message = "El RUC/NIT es obligatorio")
    @Size(min = 3, max = 50, message = "El RUC/NIT debe tener entre 3 y 50 caracteres")
    private String rucNit;

    @Size(max = 20, message = "El teléfono no puede exceder 20 caracteres")
    private String telefono;

    @Email(message = "El formato de correo es inválido")
    @Size(max = 150, message = "El email no puede exceder 150 caracteres")
    private String email;

    @Size(max = 150, message = "El contacto no puede exceder 150 caracteres")
    private String contacto;

    private TipoServicioTransporte tipoServicio;
    private Boolean activo;
}
