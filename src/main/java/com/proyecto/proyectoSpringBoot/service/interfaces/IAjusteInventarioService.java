package com.proyecto.proyectoSpringBoot.service.interfaces;

import com.proyecto.proyectoSpringBoot.dto.request.CrearAjusteRequest;
import com.proyecto.proyectoSpringBoot.dto.response.AjusteResponse;
import com.proyecto.proyectoSpringBoot.model.enums.TipoAjuste;

import java.util.List;

public interface IAjusteInventarioService {
    AjusteResponse registrarAjuste(CrearAjusteRequest request, String emailUsuario);
    List<AjusteResponse> listarTodos();
    List<AjusteResponse> listarPorBodega(Long bodegaId);
    List<AjusteResponse> listarPorProducto(Long productoId);
    List<AjusteResponse> listarPorTipo(TipoAjuste tipo);
    AjusteResponse obtenerPorId(Long id);
}
