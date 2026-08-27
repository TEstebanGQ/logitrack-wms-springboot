package com.proyecto.proyectoSpringBoot.service.impl;

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
}
