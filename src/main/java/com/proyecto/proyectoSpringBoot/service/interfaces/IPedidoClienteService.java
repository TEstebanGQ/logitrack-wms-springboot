package com.proyecto.proyectoSpringBoot.service.interfaces;

import com.proyecto.proyectoSpringBoot.dto.request.PedidoClienteRequest;
import com.proyecto.proyectoSpringBoot.dto.response.PedidoClienteResponse;
import com.proyecto.proyectoSpringBoot.model.enums.EstadoPedido;

import java.util.List;

public interface IPedidoClienteService {
    List<PedidoClienteResponse> listarTodos();
    List<PedidoClienteResponse> listarPorCliente(Long clienteId);
    List<PedidoClienteResponse> listarPorEstado(EstadoPedido estado);
    PedidoClienteResponse obtenerPorId(Long id);
    PedidoClienteResponse crear(PedidoClienteRequest request, String usuarioEmail);
    PedidoClienteResponse cambiarEstado(Long id, EstadoPedido nuevoEstado, String observaciones);
    PedidoClienteResponse despacharPedido(Long id, String usuarioEmail);
    void cancelar(Long id, String motivo);
}
