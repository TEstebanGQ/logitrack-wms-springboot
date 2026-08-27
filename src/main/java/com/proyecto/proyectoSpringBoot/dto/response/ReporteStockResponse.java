package com.proyecto.proyectoSpringBoot.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class ReporteStockResponse {
    private List<StockBodegaItem> stockPorBodega;
    private List<ProductoMovidoItem> productosMasMovidos;

    @Data @Builder @NoArgsConstructor @AllArgsConstructor
    public static class StockBodegaItem {
        private Long bodegaId;
        private String bodegaNombre;
        private Long stockTotal;
    }

    @Data @Builder @NoArgsConstructor @AllArgsConstructor
    public static class ProductoMovidoItem {
        private Long productoId;
        private String productoNombre;
        private Long totalMovido;
    }
}
