package com.proyecto.proyectoSpringBoot.service.impl;

import com.proyecto.proyectoSpringBoot.dto.request.GuiaDespachoRequest;
import com.proyecto.proyectoSpringBoot.dto.response.GuiaDespachoResponse;
import com.proyecto.proyectoSpringBoot.exception.ResourceNotFoundException;
import com.proyecto.proyectoSpringBoot.mapper.GuiaDespachoMapper;
import com.proyecto.proyectoSpringBoot.model.entity.GuiaDespacho;
import com.proyecto.proyectoSpringBoot.model.entity.PedidoCliente;
import com.proyecto.proyectoSpringBoot.model.entity.Transportadora;
import com.proyecto.proyectoSpringBoot.model.enums.EstadoEnvio;
import com.proyecto.proyectoSpringBoot.repository.GuiaDespachoRepository;
import com.proyecto.proyectoSpringBoot.repository.PedidoClienteRepository;
import com.proyecto.proyectoSpringBoot.repository.TransportadoraRepository;
import com.proyecto.proyectoSpringBoot.service.interfaces.IGuiaDespachoService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class GuiaDespachoServiceImpl implements IGuiaDespachoService {

    private final GuiaDespachoRepository guiaDespachoRepository;
    private final PedidoClienteRepository pedidoClienteRepository;
    private final TransportadoraRepository transportadoraRepository;
    private final GuiaDespachoMapper mapper;

    @Override
    @Transactional(readOnly = true)
    public List<GuiaDespachoResponse> listarTodas() {
        return guiaDespachoRepository.findAll().stream()
                .map(mapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<GuiaDespachoResponse> listarPorTransportadora(Long transportadoraId) {
        return guiaDespachoRepository.findByTransportadoraId(transportadoraId).stream()
                .map(mapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<GuiaDespachoResponse> listarPorEstado(EstadoEnvio estado) {
        return guiaDespachoRepository.findByEstadoEnvio(estado).stream()
                .map(mapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public GuiaDespachoResponse obtenerPorId(Long id) {
        GuiaDespacho g = guiaDespachoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Guía de despacho no encontrada con id: " + id));
        return mapper.toResponse(g);
    }

    @Override
    @Transactional(readOnly = true)
    public GuiaDespachoResponse obtenerPorPedido(Long pedidoId) {
        GuiaDespacho g = guiaDespachoRepository.findByPedidoId(pedidoId)
                .orElseThrow(() -> new ResourceNotFoundException("No existe guía de despacho para el pedido id: " + pedidoId));
        return mapper.toResponse(g);
    }

    @Override
    public GuiaDespachoResponse generarGuia(GuiaDespachoRequest request) {
        if (guiaDespachoRepository.existsByNumeroGuia(request.getNumeroGuia())) {
            throw new IllegalArgumentException("Ya existe una guía con el número: " + request.getNumeroGuia());
        }

        PedidoCliente pedido = pedidoClienteRepository.findById(request.getPedidoId())
                .orElseThrow(() -> new ResourceNotFoundException("Pedido no encontrado con id: " + request.getPedidoId()));

        Transportadora transportadora = transportadoraRepository.findById(request.getTransportadoraId())
                .orElseThrow(() -> new ResourceNotFoundException("Transportadora no encontrada con id: " + request.getTransportadoraId()));

        GuiaDespacho guia = GuiaDespacho.builder()
                .numeroGuia(request.getNumeroGuia().trim().toUpperCase())
                .pedido(pedido)
                .transportadora(transportadora)
                .fechaDespacho(LocalDateTime.now())
                .fechaEntregaEstimada(request.getFechaEntregaEstimada())
                .estadoEnvio(EstadoEnvio.EN_TRANSITO)
                .conductorNombre(request.getConductorNombre())
                .placaVehiculo(request.getPlacaVehiculo())
                .costoFlete(request.getCostoFlete() != null ? request.getCostoFlete() : BigDecimal.ZERO)
                .observaciones(request.getObservaciones())
                .build();

        return mapper.toResponse(guiaDespachoRepository.save(guia));
    }

    @Override
    public GuiaDespachoResponse actualizarEstado(Long id, EstadoEnvio nuevoEstado, String observaciones) {
        GuiaDespacho g = guiaDespachoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Guía de despacho no encontrada con id: " + id));

        g.setEstadoEnvio(nuevoEstado);
        if (nuevoEstado == EstadoEnvio.ENTREGADO) {
            g.setFechaEntregaReal(LocalDateTime.now());
        }
        if (observaciones != null) {
            g.setObservaciones(observaciones);
        }

        return mapper.toResponse(guiaDespachoRepository.save(g));
    }

    @Override
    public void eliminar(Long id) {
        GuiaDespacho g = guiaDespachoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Guía de despacho no encontrada con id: " + id));
        guiaDespachoRepository.delete(g);
    }
}
