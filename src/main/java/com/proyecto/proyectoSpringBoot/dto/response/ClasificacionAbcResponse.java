package com.proyecto.proyectoSpringBoot.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClasificacionAbcResponse {
    private BigDecimal valorTotalInventario;
    private int totalProductos;
    private List<ProductoAbcItem> itemsA;
    private List<ProductoAbcItem> itemsB;
    private List<ProductoAbcItem> itemsC;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ProductoAbcItem {
        private Long productoId;
        private String productoNombre;
        private String categoriaNombre;
        private Integer stock;
        private BigDecimal precio;
        private BigDecimal valorValorizado;
        private Double porcentajeValor;
        private String clasificacion; // "A", "B", "C"
    }
}
