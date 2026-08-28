package com.proyecto.proyectoSpringBoot.service.interfaces;

import com.proyecto.proyectoSpringBoot.dto.request.UnidadMedidaRequest;
import com.proyecto.proyectoSpringBoot.dto.response.UnidadMedidaResponse;

import java.util.List;

public interface IUnidadMedidaService {
    List<UnidadMedidaResponse> listarTodas();
    List<UnidadMedidaResponse> listarActivas();
    UnidadMedidaResponse obtenerPorId(Long id);
    UnidadMedidaResponse crear(UnidadMedidaRequest request);
    UnidadMedidaResponse actualizar(Long id, UnidadMedidaRequest request);
    void eliminar(Long id);
}
