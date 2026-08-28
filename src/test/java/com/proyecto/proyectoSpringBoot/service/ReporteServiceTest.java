package com.proyecto.proyectoSpringBoot.service;

import com.proyecto.proyectoSpringBoot.dto.response.AuditoriaResponse;
import com.proyecto.proyectoSpringBoot.dto.response.MovimientoResponse;
import com.proyecto.proyectoSpringBoot.dto.response.ReporteStockResponse;
import com.proyecto.proyectoSpringBoot.model.enums.TipoMovimiento;
import com.proyecto.proyectoSpringBoot.service.interfaces.IReporteService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class ReporteServiceTest {

    @Autowired
    private IReporteService reporteService;

    @Test
    @DisplayName("01. Debe generar reporte general de stock y productos más movidos")
    void testGenerarReporteGeneral() {
        ReporteStockResponse res = reporteService.generarReporteGeneral();
        assertNotNull(res);
        assertNotNull(res.getStockPorBodega());
        assertNotNull(res.getProductosMasMovidos());
    }

    @Test
    @DisplayName("02. Debe filtrar movimientos por tipo de movimiento ENTRADA")
    void testFiltrarMovimientosPorTipo() {
        List<MovimientoResponse> res = reporteService.consultarMovimientosFiltrados(
                null, null, TipoMovimiento.ENTRADA, null, null);
        assertNotNull(res);
        assertTrue(res.stream().allMatch(m -> m.getTipoMovimiento() == TipoMovimiento.ENTRADA));
    }

    @Test
    @DisplayName("03. Debe filtrar movimientos por bodega")
    void testFiltrarMovimientosPorBodega() {
        List<MovimientoResponse> res = reporteService.consultarMovimientosFiltrados(
                1L, null, null, null, null);
        assertNotNull(res);
    }

    @Test
    @DisplayName("04. Debe filtrar movimientos por producto")
    void testFiltrarMovimientosPorProducto() {
        List<MovimientoResponse> res = reporteService.consultarMovimientosFiltrados(
                null, 1L, null, null, null);
        assertNotNull(res);
    }

    @Test
    @DisplayName("05. Debe filtrar movimientos por rango de fechas")
    void testFiltrarMovimientosPorFechas() {
        LocalDateTime inicio = LocalDateTime.now().minusDays(30);
        LocalDateTime fin = LocalDateTime.now().plusDays(1);
        List<MovimientoResponse> res = reporteService.consultarMovimientosFiltrados(
                null, null, null, inicio, fin);
        assertNotNull(res);
    }

    @Test
    @DisplayName("06. Debe consultar auditorías con filtros opcionales")
    void testConsultarAuditoriasFiltradas() {
        List<AuditoriaResponse> res = reporteService.consultarAuditoriaFiltrada(
                1L, LocalDateTime.now().minusDays(30), LocalDateTime.now().plusDays(1), null);
        assertNotNull(res);
    }
}
