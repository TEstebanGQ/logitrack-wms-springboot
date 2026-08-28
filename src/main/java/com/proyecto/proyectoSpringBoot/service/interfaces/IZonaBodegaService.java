package com.proyecto.proyectoSpringBoot.service.interfaces;

import com.proyecto.proyectoSpringBoot.dto.request.ZonaBodegaRequest;
import com.proyecto.proyectoSpringBoot.dto.response.ZonaBodegaResponse;
import com.proyecto.proyectoSpringBoot.model.enums.TipoZona;

import java.util.List;

public interface IZonaBodegaService {
    List<ZonaBodegaResponse> listarTodas();
    List<ZonaBodegaResponse> listarPorBodega(Long bodegaId);
    List<ZonaBodegaResponse> listarPorTipo(TipoZona tipoZona);
    ZonaBodegaResponse obtenerPorId(Long id);
    ZonaBodegaResponse crear(ZonaBodegaRequest request);
    ZonaBodegaResponse actualizar(Long id, ZonaBodegaRequest request);
    void eliminar(Long id);
}
