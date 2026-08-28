package com.proyecto.proyectoSpringBoot.dto.request;

import com.proyecto.proyectoSpringBoot.model.enums.TipoZona;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class ZonaBodegaRequest {
    @NotBlank(message = "El código es obligatorio")
    private String codigo;

    @NotBlank(message = "El nombre es obligatorio")
    private String nombre;

    @NotNull(message = "El tipo de zona es obligatorio")
    private TipoZona tipoZona;

    @NotNull(message = "El ID de la bodega es obligatorio")
    private Long bodegaId;

    private Boolean temperaturaControlada;
    private String descripcion;
    private Boolean activo;
}
