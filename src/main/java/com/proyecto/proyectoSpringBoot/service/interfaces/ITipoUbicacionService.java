package com.proyecto.proyectoSpringBoot.service.interfaces;

import com.proyecto.proyectoSpringBoot.dto.request.TipoUbicacionRequest;
import com.proyecto.proyectoSpringBoot.dto.response.TipoUbicacionResponse;

import java.util.List;

public interface ITipoUbicacionService {
    List<TipoUbicacionResponse> listarTodas();
    List<TipoUbicacionResponse> listarActivas();
    TipoUbicacionResponse obtenerPorId(Long id);
    TipoUbicacionResponse crear(TipoUbicacionRequest request);
    TipoUbicacionResponse actualizar(Long id, TipoUbicacionRequest request);
    void eliminar(Long id);
}
