package com.proyecto.proyectoSpringBoot.service.impl;

import com.proyecto.proyectoSpringBoot.dto.request.TareaPickingRequest;
import com.proyecto.proyectoSpringBoot.dto.response.TareaPickingResponse;
import com.proyecto.proyectoSpringBoot.exception.ResourceNotFoundException;
import com.proyecto.proyectoSpringBoot.mapper.TareaPickingMapper;
import com.proyecto.proyectoSpringBoot.model.entity.*;
import com.proyecto.proyectoSpringBoot.model.enums.EstadoPicking;
import com.proyecto.proyectoSpringBoot.repository.*;
import com.proyecto.proyectoSpringBoot.service.interfaces.ITareaPickingService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class TareaPickingServiceImpl implements ITareaPickingService {

    private final TareaPickingRepository tareaPickingRepository;
    private final PedidoClienteRepository pedidoClienteRepository;
    private final ProductoRepository productoRepository;
    private final UbicacionBodegaRepository ubicacionBodegaRepository;
    private final UsuarioRepository usuarioRepository;
    private final TareaPickingMapper mapper;

    @Override
    @Transactional(readOnly = true)
    public List<TareaPickingResponse> listarTodas() {
        return tareaPickingRepository.findAll().stream()
                .map(mapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<TareaPickingResponse> listarPorPedido(Long pedidoId) {
        return tareaPickingRepository.findByPedidoId(pedidoId).stream()
                .map(mapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<TareaPickingResponse> listarPorUsuario(Long usuarioId) {
        return tareaPickingRepository.findByUsuarioAsignadoId(usuarioId).stream()
                .map(mapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<TareaPickingResponse> listarPorEstado(EstadoPicking estado) {
        return tareaPickingRepository.findByEstado(estado).stream()
                .map(mapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public TareaPickingResponse obtenerPorId(Long id) {
        TareaPicking t = tareaPickingRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Tarea de picking no encontrada con id: " + id));
        return mapper.toResponse(t);
    }

    @Override
    public TareaPickingResponse crear(TareaPickingRequest request) {
        PedidoCliente pedido = pedidoClienteRepository.findById(request.getPedidoId())
                .orElseThrow(() -> new ResourceNotFoundException("Pedido no encontrado con id: " + request.getPedidoId()));

        Producto producto = productoRepository.findById(request.getProductoId())
                .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado con id: " + request.getProductoId()));

        UbicacionBodega ubicacion = null;
        if (request.getUbicacionOrigenId() != null) {
            ubicacion = ubicacionBodegaRepository.findById(request.getUbicacionOrigenId())
                    .orElseThrow(() -> new ResourceNotFoundException("Ubicación no encontrada con id: " + request.getUbicacionOrigenId()));
        }

        Usuario usuario = null;
        if (request.getUsuarioAsignadoId() != null) {
            usuario = usuarioRepository.findById(request.getUsuarioAsignadoId())
                    .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado con id: " + request.getUsuarioAsignadoId()));
        }

        String codigoTarea = "PCK-" + System.currentTimeMillis();

        TareaPicking t = TareaPicking.builder()
                .codigoTarea(codigoTarea)
                .pedido(pedido)
                .producto(producto)
                .ubicacionOrigen(ubicacion)
                .usuarioAsignado(usuario)
                .cantidadRequerida(request.getCantidadRequerida())
                .cantidadRecogida(0)
                .estado(EstadoPicking.PENDIENTE)
                .fechaAsignacion(LocalDateTime.now())
                .notas(request.getNotas())
                .build();

        return mapper.toResponse(tareaPickingRepository.save(t));
    }

    @Override
    public TareaPickingResponse actualizarRecoleccion(Long id, Integer cantidadRecogida, String notas) {
        TareaPicking t = tareaPickingRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Tarea de picking no encontrada con id: " + id));

        t.setCantidadRecogida(cantidadRecogida);
        if (notas != null) t.setNotas(notas);

        if (cantidadRecogida >= t.getCantidadRequerida()) {
            t.setEstado(EstadoPicking.COMPLETADA);
            t.setFechaCompletada(LocalDateTime.now());
        } else if (cantidadRecogida > 0) {
            t.setEstado(EstadoPicking.EN_PROCESO);
        }

        return mapper.toResponse(tareaPickingRepository.save(t));
    }

    @Override
    public TareaPickingResponse cambiarEstado(Long id, EstadoPicking nuevoEstado) {
        TareaPicking t = tareaPickingRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Tarea de picking no encontrada con id: " + id));

        t.setEstado(nuevoEstado);
        if (nuevoEstado == EstadoPicking.COMPLETADA) {
            t.setFechaCompletada(LocalDateTime.now());
        }

        return mapper.toResponse(tareaPickingRepository.save(t));
    }

    @Override
    public void eliminar(Long id) {
        TareaPicking t = tareaPickingRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Tarea de picking no encontrada con id: " + id));
        t.setEstado(EstadoPicking.CANCELADA);
        tareaPickingRepository.save(t);
    }
}
