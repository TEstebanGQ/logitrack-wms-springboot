package com.proyecto.proyectoSpringBoot.service.interfaces;

import com.proyecto.proyectoSpringBoot.dto.request.GuiaDespachoRequest;
import com.proyecto.proyectoSpringBoot.dto.response.GuiaDespachoResponse;
import com.proyecto.proyectoSpringBoot.model.enums.EstadoEnvio;

import java.util.List;

public interface IGuiaDespachoService {
    List<GuiaDespachoResponse> listarTodas();
    List<GuiaDespachoResponse> listarPorTransportadora(Long transportadoraId);
    List<GuiaDespachoResponse> listarPorEstado(EstadoEnvio estado);
    GuiaDespachoResponse obtenerPorId(Long id);
    GuiaDespachoResponse obtenerPorPedido(Long pedidoId);
    GuiaDespachoResponse generarGuia(GuiaDespachoRequest request);
    GuiaDespachoResponse actualizarEstado(Long id, EstadoEnvio nuevoEstado, String observaciones);
    void eliminar(Long id);
}
