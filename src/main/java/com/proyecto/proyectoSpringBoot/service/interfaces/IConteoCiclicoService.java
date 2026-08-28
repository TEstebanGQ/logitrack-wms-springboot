package com.proyecto.proyectoSpringBoot.service.interfaces;

import com.proyecto.proyectoSpringBoot.dto.request.ConteoCiclicoRequest;
import com.proyecto.proyectoSpringBoot.dto.response.ConteoCiclicoResponse;
import com.proyecto.proyectoSpringBoot.model.enums.EstadoConteo;

import java.util.List;

public interface IConteoCiclicoService {
    List<ConteoCiclicoResponse> listarTodos();
    List<ConteoCiclicoResponse> listarPorBodega(Long bodegaId);
    List<ConteoCiclicoResponse> listarPorEstado(EstadoConteo estado);
    ConteoCiclicoResponse obtenerPorId(Long id);
    ConteoCiclicoResponse crear(ConteoCiclicoRequest request, String supervisorEmail);
    ConteoCiclicoResponse registrarConteoFisico(Long id, Long detalleId, Integer stockFisico, String notas);
    ConteoCiclicoResponse conciliarYCerrar(Long id, String supervisorEmail);
    void eliminar(Long id);
}
