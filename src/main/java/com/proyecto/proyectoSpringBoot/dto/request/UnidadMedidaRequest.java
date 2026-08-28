package com.proyecto.proyectoSpringBoot.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class UnidadMedidaRequest {
    @NotBlank(message = "El código es obligatorio")
    private String codigo;

    @NotBlank(message = "El nombre es obligatorio")
    private String nombre;

    private String abreviatura;

    @NotNull(message = "El factor de conversión es obligatorio")
    @Positive(message = "El factor de conversión debe ser positivo")
    private Double factorConversion;

    private Boolean activo;
}
