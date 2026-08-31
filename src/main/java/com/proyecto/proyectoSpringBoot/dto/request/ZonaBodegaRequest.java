package com.proyecto.proyectoSpringBoot.dto.request;

import com.proyecto.proyectoSpringBoot.model.enums.TipoZona;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class ZonaBodegaRequest {
    @NotBlank(message = "El código es obligatorio")
    @Size(min = 2, max = 50, message = "El código debe tener entre 2 y 50 caracteres")
    private String codigo;

    @NotBlank(message = "El nombre es obligatorio")
    @Size(min = 2, max = 100, message = "El nombre debe tener entre 2 y 100 caracteres")
    private String nombre;

    @NotNull(message = "El tipo de zona es obligatorio")
    private TipoZona tipoZona;

    @NotNull(message = "El ID de la bodega es obligatorio")
    private Long bodegaId;

    private Boolean temperaturaControlada;

    @Size(max = 255, message = "La descripción no puede exceder 255 caracteres")
    private String descripcion;
    private Boolean activo;
}
