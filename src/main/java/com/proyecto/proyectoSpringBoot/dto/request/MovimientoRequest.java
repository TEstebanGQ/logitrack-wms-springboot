package com.proyecto.proyectoSpringBoot.dto.request;

import com.proyecto.proyectoSpringBoot.model.enums.TipoMovimiento;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.List;

@Data
public class MovimientoRequest {

    @NotNull
    private TipoMovimiento tipoMovimiento;

    private Long bodegaOrigenId;
    private Long bodegaDestinoId;

    @Size(max = 500)
    private String observaciones;

    @NotEmpty @Valid
    private List<DetalleRequest> detalles;

    @Data
    public static class DetalleRequest {
        @NotNull
        private Long productoId;
        @NotNull @Min(1)
        private Integer cantidad;
    }
}
