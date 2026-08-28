package com.proyecto.proyectoSpringBoot.dto.request;

import com.proyecto.proyectoSpringBoot.model.enums.TipoServicioTransporte;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class TransportadoraRequest {
    @NotBlank(message = "El nombre es obligatorio")
    private String nombre;

    @NotBlank(message = "El RUC/NIT es obligatorio")
    private String rucNit;

    private String telefono;
    private String email;
    private String contacto;
    private TipoServicioTransporte tipoServicio;
    private Boolean activo;
}
