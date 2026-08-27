package com.proyecto.proyectoSpringBoot.service.interfaces;

import com.proyecto.proyectoSpringBoot.dto.request.CrearBodegaRequest;
import com.proyecto.proyectoSpringBoot.dto.response.BodegaResponse;
import com.proyecto.proyectoSpringBoot.dto.response.InventarioBodegaResponse;

import java.util.List;

public interface IBodegaService {
    BodegaResponse crear(CrearBodegaRequest request);
    BodegaResponse obtenerPorId(Long id);
    List<BodegaResponse> listarTodas();
    List<BodegaResponse> listarActivas();
    List<InventarioBodegaResponse> obtenerInventarioPorBodega(Long bodegaId);
    BodegaResponse actualizar(Long id, CrearBodegaRequest request);
    void eliminar(Long id);
}
