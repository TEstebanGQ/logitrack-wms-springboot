package com.proyecto.proyectoSpringBoot.service.interfaces;

import com.proyecto.proyectoSpringBoot.dto.response.AuditoriaResponse;
import com.proyecto.proyectoSpringBoot.model.enums.TipoOperacion;

import java.time.LocalDateTime;
import java.util.List;

public interface IAuditoriaService {
    List<AuditoriaResponse> listarTodas();
    List<AuditoriaResponse> listarPorUsuario(Long usuarioId);
    List<AuditoriaResponse> listarPorTipoOperacion(TipoOperacion tipo);
    List<AuditoriaResponse> listarPorEntidad(String entidad);
    List<AuditoriaResponse> listarPorRangoFechas(LocalDateTime inicio, LocalDateTime fin);
}
