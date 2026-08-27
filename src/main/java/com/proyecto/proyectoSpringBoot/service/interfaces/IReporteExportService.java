package com.proyecto.proyectoSpringBoot.service.interfaces;

import java.time.LocalDate;

public interface IReporteExportService {
    byte[] exportarResumenExcel();
    byte[] exportarResumenPdf();
    byte[] exportarMovimientosExcel(LocalDate desde, LocalDate hasta);
    byte[] exportarMovimientosPdf(LocalDate desde, LocalDate hasta);
}
