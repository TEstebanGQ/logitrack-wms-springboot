package com.proyecto.proyectoSpringBoot.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class UnidadMedidaRequest {
    @NotBlank(message = "El código es obligatorio")
    @Size(min = 1, max = 20, message = "El código debe tener entre 1 y 20 caracteres")
    private String codigo;

    @NotBlank(message = "El nombre es obligatorio")
    @Size(min = 2, max = 100, message = "El nombre debe tener entre 2 y 100 caracteres")
    private String nombre;

    @Size(max = 20, message = "La abreviatura no puede exceder 20 caracteres")
    private String abreviatura;

    @NotNull(message = "El factor de conversión es obligatorio")
    @Positive(message = "El factor de conversión debe ser positivo")
    private Double factorConversion;

    private Boolean activo;
}
