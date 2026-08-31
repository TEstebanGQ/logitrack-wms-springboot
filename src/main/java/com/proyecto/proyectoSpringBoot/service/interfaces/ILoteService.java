package com.proyecto.proyectoSpringBoot.service.interfaces;

import com.proyecto.proyectoSpringBoot.dto.request.CrearLoteRequest;
import com.proyecto.proyectoSpringBoot.dto.response.LoteResponse;
import com.proyecto.proyectoSpringBoot.model.enums.EstadoLote;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ILoteService {
    LoteResponse crearLote(CrearLoteRequest request, String emailUsuario);
    List<LoteResponse> listarTodos();
    Page<LoteResponse> listarTodos(Pageable pageable);
    List<LoteResponse> listarPorProducto(Long productoId);
    List<LoteResponse> listarPorBodega(Long bodegaId);
    List<LoteResponse> listarPorProductoYBodegaFEFO(Long productoId, Long bodegaId);
    List<LoteResponse> listarProximosAVencer(int dias);
    LoteResponse obtenerPorId(Long id);
    LoteResponse actualizarEstado(Long id, EstadoLote estado, String emailUsuario);
}
