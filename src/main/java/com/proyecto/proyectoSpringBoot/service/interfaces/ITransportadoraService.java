package com.proyecto.proyectoSpringBoot.service.interfaces;

import com.proyecto.proyectoSpringBoot.dto.request.TransportadoraRequest;
import com.proyecto.proyectoSpringBoot.dto.response.TransportadoraResponse;

import java.util.List;

public interface ITransportadoraService {
    List<TransportadoraResponse> listarTodas();
    List<TransportadoraResponse> listarActivas();
    TransportadoraResponse obtenerPorId(Long id);
    TransportadoraResponse crear(TransportadoraRequest request);
    TransportadoraResponse actualizar(Long id, TransportadoraRequest request);
    void eliminar(Long id);
}
