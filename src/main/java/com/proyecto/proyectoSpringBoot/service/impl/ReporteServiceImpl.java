package com.proyecto.proyectoSpringBoot.service.impl;

import com.proyecto.proyectoSpringBoot.dto.response.AuditoriaResponse;
import com.proyecto.proyectoSpringBoot.dto.response.ClasificacionAbcResponse;
import com.proyecto.proyectoSpringBoot.dto.response.MovimientoResponse;
import com.proyecto.proyectoSpringBoot.dto.response.ReporteStockResponse;
import com.proyecto.proyectoSpringBoot.mapper.AuditoriaMapper;
import com.proyecto.proyectoSpringBoot.mapper.MovimientoMapper;
import com.proyecto.proyectoSpringBoot.mapper.ProductoMapper;
import com.proyecto.proyectoSpringBoot.dto.response.ProductoResponse;
import com.proyecto.proyectoSpringBoot.model.entity.Auditoria;
import com.proyecto.proyectoSpringBoot.model.entity.Movimiento;
import com.proyecto.proyectoSpringBoot.model.entity.Producto;
import com.proyecto.proyectoSpringBoot.model.enums.TipoMovimiento;
import com.proyecto.proyectoSpringBoot.repository.AuditoriaRepository;
import com.proyecto.proyectoSpringBoot.repository.InventarioBodegaRepository;
import com.proyecto.proyectoSpringBoot.repository.MovimientoRepository;
import com.proyecto.proyectoSpringBoot.repository.ProductoRepository;
import com.proyecto.proyectoSpringBoot.service.interfaces.IReporteService;
import com.proyecto.proyectoSpringBoot.specification.AuditoriaSpecification;
import com.proyecto.proyectoSpringBoot.specification.MovimientoSpecification;
import com.proyecto.proyectoSpringBoot.specification.ProductoSpecification;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReporteServiceImpl implements IReporteService {

    private final InventarioBodegaRepository inventarioRepository;
    private final MovimientoRepository movimientoRepository;
    private final AuditoriaRepository auditoriaRepository;
    private final ProductoRepository productoRepository;
    private final MovimientoMapper movimientoMapper;
    private final AuditoriaMapper auditoriaMapper;
    private final ProductoMapper productoMapper;

    @Override
    @Cacheable(value = "reporteStockGeneral", key = "'general'")
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
    @Cacheable(value = "reporteABC", key = "'general'")
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

    @Override
    public List<MovimientoResponse> consultarMovimientosFiltrados(
            Long bodegaId, Long productoId, TipoMovimiento tipoMovimiento, LocalDateTime fechaInicio, LocalDateTime fechaFin) {

        Specification<Movimiento> spec = Specification.where(MovimientoSpecification.porTipo(tipoMovimiento))
                .and(MovimientoSpecification.entFechas(fechaInicio, fechaFin))
                .and(MovimientoSpecification.porBodega(bodegaId))
                .and(MovimientoSpecification.porProducto(productoId));

        return movimientoRepository.findAll(spec).stream()
                .map(movimientoMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<AuditoriaResponse> consultarAuditoriaFiltrada(
            Long productoId, LocalDateTime fechaInicio, LocalDateTime fechaFin, String campoModificado) {

        Specification<Auditoria> spec = Specification.where(AuditoriaSpecification.porProducto(productoId))
                .and(AuditoriaSpecification.entFechas(fechaInicio, fechaFin))
                .and(AuditoriaSpecification.porCampoModificado(campoModificado));

        return auditoriaRepository.findAll(spec).stream()
                .map(auditoriaMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<ProductoResponse> consultarInventarioFiltrado(String nombre, Integer stockMin, Integer stockMax) {
        Specification<Producto> spec = Specification.where(ProductoSpecification.nombreContiene(nombre))
                .and(ProductoSpecification.stockEntre(stockMin, stockMax));

        return productoRepository.findAll(spec).stream()
                .map(productoMapper::toResponse)
                .collect(Collectors.toList());
    }
}
