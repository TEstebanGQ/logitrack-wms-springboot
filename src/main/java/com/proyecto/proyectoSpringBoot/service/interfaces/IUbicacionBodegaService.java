package com.proyecto.proyectoSpringBoot.service.interfaces;

import com.proyecto.proyectoSpringBoot.dto.request.CrearUbicacionRequest;
import com.proyecto.proyectoSpringBoot.dto.response.UbicacionBodegaResponse;

import java.util.List;

public interface IUbicacionBodegaService {
    UbicacionBodegaResponse crearUbicacion(CrearUbicacionRequest request, String emailUsuario);
    List<UbicacionBodegaResponse> listarTodas();
    List<UbicacionBodegaResponse> listarPorBodega(Long bodegaId, boolean soloActivos);
    UbicacionBodegaResponse obtenerPorId(Long id);
    void desactivarUbicacion(Long id, String emailUsuario);
}
