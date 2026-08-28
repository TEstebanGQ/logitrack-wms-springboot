package com.proyecto.proyectoSpringBoot.service.interfaces;

import com.proyecto.proyectoSpringBoot.dto.request.CrearProveedorRequest;
import com.proyecto.proyectoSpringBoot.dto.response.ProveedorResponse;
import com.proyecto.proyectoSpringBoot.dto.response.MovimientoResponse;
import java.util.List;

public interface IProveedorService {
    ProveedorResponse crear(CrearProveedorRequest request);
    List<ProveedorResponse> listar();
    List<ProveedorResponse> listar(boolean soloActivos);
    ProveedorResponse obtenerPorId(Long id);
    ProveedorResponse actualizar(Long id, CrearProveedorRequest request);
    void eliminar(Long id);
    List<MovimientoResponse> listarMovimientosProveedor(Long id);
}
