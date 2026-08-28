package com.proyecto.proyectoSpringBoot.service.impl;

import com.proyecto.proyectoSpringBoot.dto.response.ClasificacionAbcResponse;
import com.proyecto.proyectoSpringBoot.dto.response.ReporteStockResponse;
import com.proyecto.proyectoSpringBoot.repository.InventarioBodegaRepository;
import com.proyecto.proyectoSpringBoot.repository.MovimientoRepository;
import com.proyecto.proyectoSpringBoot.repository.ProductoRepository;
import com.proyecto.proyectoSpringBoot.service.interfaces.IReporteService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReporteServiceImpl implements IReporteService {

    private final InventarioBodegaRepository inventarioRepository;
    private final MovimientoRepository movimientoRepository;
    private final ProductoRepository productoRepository;

    @Override
    public ReporteStockResponse generarReporteGeneral() {
        List<ReporteStockResponse.StockBodegaItem> stockPorBodega =
                inventarioRepository.findStockTotalPorBodega().stream()
                        .map(row -> ReporteStockResponse.StockBodegaItem.builder()
                                .bodegaId(((Number) row[0]).longValue())
                                .bodegaNombre((String) row[1])
                                .stockTotal(((Number) row[2]).longValue())
                                .build())
                        .collect(Collectors.toList());

        List<ReporteStockResponse.ProductoMovidoItem> masMovidos =
                movimientoRepository.findProductosMasMovidos().stream()
                        .limit(10)
                        .map(row -> {
                            Long productoId = ((Number) row[0]).longValue();
                            String nombre = productoRepository.findById(productoId)
                                    .map(p -> p.getNombre()).orElse("Desconocido");
                            return ReporteStockResponse.ProductoMovidoItem.builder()
                                    .productoId(productoId)
                                    .productoNombre(nombre)
                                    .totalMovido(((Number) row[1]).longValue())
                                    .build();
                        })
                        .collect(Collectors.toList());

        return ReporteStockResponse.builder()
                .stockPorBodega(stockPorBodega)
                .productosMasMovidos(masMovidos)
                .build();
    }

    @Override
    public ClasificacionAbcResponse calcularClasificacionABC() {
        List<com.proyecto.proyectoSpringBoot.model.entity.Producto> productos = productoRepository.findByActivoTrue();

        java.math.BigDecimal totalValor = java.math.BigDecimal.ZERO;
        List<ClasificacionAbcResponse.ProductoAbcItem> items = new java.util.ArrayList<>();

        for (com.proyecto.proyectoSpringBoot.model.entity.Producto p : productos) {
            java.math.BigDecimal precio = p.getPrecio() != null ? p.getPrecio() : java.math.BigDecimal.ZERO;
            int stock = p.getStock() != null ? p.getStock() : 0;
            java.math.BigDecimal valor = precio.multiply(java.math.BigDecimal.valueOf(stock));
            totalValor = totalValor.add(valor);

            items.add(ClasificacionAbcResponse.ProductoAbcItem.builder()
                    .productoId(p.getId())
                    .productoNombre(p.getNombre())
                    .categoriaNombre(p.getCategoria() != null ? p.getCategoria().getNombre() : "General")
                    .stock(stock)
                    .precio(precio)
                    .valorValorizado(valor)
                    .build());
        }

        items.sort((a, b) -> b.getValorValorizado().compareTo(a.getValorValorizado()));

        List<ClasificacionAbcResponse.ProductoAbcItem> itemsA = new java.util.ArrayList<>();
        List<ClasificacionAbcResponse.ProductoAbcItem> itemsB = new java.util.ArrayList<>();
        List<ClasificacionAbcResponse.ProductoAbcItem> itemsC = new java.util.ArrayList<>();

        double acumulado = 0.0;
        double granTotal = totalValor.doubleValue() > 0 ? totalValor.doubleValue() : 1.0;

        for (ClasificacionAbcResponse.ProductoAbcItem item : items) {
            double pct = (item.getValorValorizado().doubleValue() / granTotal) * 100.0;
            item.setPorcentajeValor(Math.round(pct * 100.0) / 100.0);
            acumulado += pct;

            if (acumulado <= 80.0 || itemsA.isEmpty()) {
                item.setClasificacion("A");
                itemsA.add(item);
            } else if (acumulado <= 95.0) {
                item.setClasificacion("B");
                itemsB.add(item);
            } else {
                item.setClasificacion("C");
                itemsC.add(item);
            }
        }

        return ClasificacionAbcResponse.builder()
                .valorTotalInventario(totalValor)
                .totalProductos(productos.size())
                .itemsA(itemsA)
                .itemsB(itemsB)
                .itemsC(itemsC)
                .build();
    }
}
