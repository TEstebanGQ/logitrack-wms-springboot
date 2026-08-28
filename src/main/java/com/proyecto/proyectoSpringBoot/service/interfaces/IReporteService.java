package com.proyecto.proyectoSpringBoot.service.interfaces;

import com.proyecto.proyectoSpringBoot.dto.response.AuditoriaResponse;
import com.proyecto.proyectoSpringBoot.dto.response.ClasificacionAbcResponse;
import com.proyecto.proyectoSpringBoot.dto.response.MovimientoResponse;
import com.proyecto.proyectoSpringBoot.dto.response.ReporteStockResponse;
import com.proyecto.proyectoSpringBoot.model.enums.TipoMovimiento;

import java.time.LocalDateTime;
import java.util.List;

public interface IReporteService {
    ReporteStockResponse generarReporteGeneral();
    ClasificacionAbcResponse calcularClasificacionABC();
    List<MovimientoResponse> consultarMovimientosFiltrados(Long bodega, Long producto, TipoMovimiento tipoMovimiento, LocalDateTime fechaInicio, LocalDateTime fechaFin);
    List<AuditoriaResponse> consultarAuditoriaFiltrada(Long producto, LocalDateTime fechaInicio, LocalDateTime fechaFin, String campoModificado);
}
