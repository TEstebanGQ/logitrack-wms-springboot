package com.proyecto.proyectoSpringBoot.dto.request;

import com.proyecto.proyectoSpringBoot.model.enums.EstadoLote;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CrearLoteRequest {

    @NotBlank(message = "El código de lote es obligatorio")
    private String codigoLote;

    @NotNull(message = "El producto es obligatorio")
    private Long productoId;

    @NotNull(message = "La bodega es obligatoria")
    private Long bodegaId;

    @NotNull(message = "La cantidad inicial es obligatoria")
    @Positive(message = "La cantidad inicial debe ser mayor a cero")
    private Integer stockInicial;

    private LocalDate fechaFabricacion;

    private LocalDate fechaVencimiento;

    private EstadoLote estado;
}
