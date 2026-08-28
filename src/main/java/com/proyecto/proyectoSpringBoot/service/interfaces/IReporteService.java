package com.proyecto.proyectoSpringBoot.service.interfaces;

import com.proyecto.proyectoSpringBoot.dto.response.ClasificacionAbcResponse;
import com.proyecto.proyectoSpringBoot.dto.response.ReporteStockResponse;

public interface IReporteService {
    ReporteStockResponse generarReporteGeneral();
    ClasificacionAbcResponse calcularClasificacionABC();
}
