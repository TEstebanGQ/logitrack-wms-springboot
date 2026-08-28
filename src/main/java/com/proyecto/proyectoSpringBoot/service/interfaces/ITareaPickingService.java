package com.proyecto.proyectoSpringBoot.service.interfaces;

import com.proyecto.proyectoSpringBoot.dto.request.TareaPickingRequest;
import com.proyecto.proyectoSpringBoot.dto.response.TareaPickingResponse;
import com.proyecto.proyectoSpringBoot.model.enums.EstadoPicking;

import java.util.List;

public interface ITareaPickingService {
    List<TareaPickingResponse> listarTodas();
    List<TareaPickingResponse> listarPorPedido(Long pedidoId);
    List<TareaPickingResponse> listarPorUsuario(Long usuarioId);
    List<TareaPickingResponse> listarPorEstado(EstadoPicking estado);
    TareaPickingResponse obtenerPorId(Long id);
    TareaPickingResponse crear(TareaPickingRequest request);
    TareaPickingResponse actualizarRecoleccion(Long id, Integer cantidadRecogida, String notas);
    TareaPickingResponse cambiarEstado(Long id, EstadoPicking nuevoEstado);
    void eliminar(Long id);
}
