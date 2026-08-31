package com.proyecto.proyectoSpringBoot.service.interfaces;

import com.proyecto.proyectoSpringBoot.dto.request.MovimientoRequest;
import com.proyecto.proyectoSpringBoot.dto.response.MovimientoResponse;
import com.proyecto.proyectoSpringBoot.model.enums.TipoMovimiento;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;

public interface IMovimientoService {
    MovimientoResponse registrar(MovimientoRequest request, String emailUsuario);
    MovimientoResponse obtenerPorId(Long id);
    List<MovimientoResponse> listarTodos();
    Page<MovimientoResponse> listarTodos(Pageable pageable);
    List<MovimientoResponse> listarPorTipo(TipoMovimiento tipo);
    List<MovimientoResponse> listarPorRangoFechas(LocalDateTime inicio, LocalDateTime fin);
    List<MovimientoResponse> listarPorUsuario(Long usuarioId);
    List<MovimientoResponse> listarPorBodega(Long bodegaId);
}
