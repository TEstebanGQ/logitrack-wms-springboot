package com.proyecto.proyectoSpringBoot.service.interfaces;

import com.proyecto.proyectoSpringBoot.dto.request.CrearSolicitudRequest;
import com.proyecto.proyectoSpringBoot.dto.response.SolicitudTransferenciaResponse;
import java.util.List;

public interface ISolicitudTransferenciaService {
    SolicitudTransferenciaResponse crear(CrearSolicitudRequest request, String emailSolicitante);
    SolicitudTransferenciaResponse obtenerPorId(Long id);
    List<SolicitudTransferenciaResponse> listarTodas();
    List<SolicitudTransferenciaResponse> listarPropias(String email);
    List<SolicitudTransferenciaResponse> listarPendientes();
    void aprobar(Long id, String emailAdmin, String observaciones);
    void rechazar(Long id, String emailAdmin, String motivo);
    void cancelar(Long id, String emailUsuario);
}
