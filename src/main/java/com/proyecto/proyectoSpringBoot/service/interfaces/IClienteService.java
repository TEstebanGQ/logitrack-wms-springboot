package com.proyecto.proyectoSpringBoot.service.interfaces;

import com.proyecto.proyectoSpringBoot.dto.request.CrearClienteRequest;
import com.proyecto.proyectoSpringBoot.dto.response.ClienteResponse;
import com.proyecto.proyectoSpringBoot.dto.response.MovimientoResponse;
import java.util.List;

public interface IClienteService {
    ClienteResponse crear(CrearClienteRequest request);
    List<ClienteResponse> listar();
    List<ClienteResponse> listar(boolean soloActivos);
    ClienteResponse obtenerPorId(Long id);
    ClienteResponse actualizar(Long id, CrearClienteRequest request);
    void eliminar(Long id);
    List<MovimientoResponse> listarMovimientosCliente(Long id);
}
