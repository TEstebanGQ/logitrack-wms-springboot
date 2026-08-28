package com.proyecto.proyectoSpringBoot.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CrearOrdenCompraRequest {

    @NotNull(message = "El proveedor es obligatorio")
    private Long proveedorId;

    @NotNull(message = "La bodega de destino es obligatoria")
    private Long bodegaDestinoId;

    private LocalDate fechaEntregaEsperada;

    private String observaciones;

    @NotEmpty(message = "La orden de compra debe incluir al menos un producto")
    @Valid
    private List<DetalleOrdenRequest> detalles;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DetalleOrdenRequest {
        @NotNull(message = "El producto es obligatorio")
        private Long productoId;

        @NotNull(message = "La cantidad es obligatoria")
        @Positive(message = "La cantidad debe ser mayor a cero")
        private Integer cantidad;

        @NotNull(message = "El precio unitario pactado es obligatorio")
        @Positive(message = "El precio debe ser positivo")
        private BigDecimal precioUnitario;
    }
}
