package com.proyecto.proyectoSpringBoot.service.impl;

import com.proyecto.proyectoSpringBoot.dto.request.TransportadoraRequest;
import com.proyecto.proyectoSpringBoot.dto.response.TransportadoraResponse;
import com.proyecto.proyectoSpringBoot.exception.ResourceNotFoundException;
import com.proyecto.proyectoSpringBoot.mapper.TransportadoraMapper;
import com.proyecto.proyectoSpringBoot.model.entity.Transportadora;
import com.proyecto.proyectoSpringBoot.model.enums.TipoServicioTransporte;
import com.proyecto.proyectoSpringBoot.repository.TransportadoraRepository;
import com.proyecto.proyectoSpringBoot.service.interfaces.ITransportadoraService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class TransportadoraServiceImpl implements ITransportadoraService {

    private final TransportadoraRepository transportadoraRepository;
    private final TransportadoraMapper mapper;

    @Override
    @Transactional(readOnly = true)
    public List<TransportadoraResponse> listarTodas() {
        return transportadoraRepository.findAll().stream()
                .map(mapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<TransportadoraResponse> listarActivas() {
        return transportadoraRepository.findByActivoTrue().stream()
                .map(mapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public TransportadoraResponse obtenerPorId(Long id) {
        Transportadora t = transportadoraRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Transportadora no encontrada con id: " + id));
        return mapper.toResponse(t);
    }

    @Override
    public TransportadoraResponse crear(TransportadoraRequest request) {
        if (transportadoraRepository.existsByRucNit(request.getRucNit())) {
            throw new IllegalArgumentException("Ya existe una transportadora con el RUC/NIT: " + request.getRucNit());
        }

        Transportadora t = Transportadora.builder()
                .nombre(request.getNombre().trim())
                .rucNit(request.getRucNit().trim())
                .telefono(request.getTelefono())
                .email(request.getEmail())
                .contacto(request.getContacto())
                .tipoServicio(request.getTipoServicio() != null ? request.getTipoServicio() : TipoServicioTransporte.TERRESTRE)
                .activo(request.getActivo() != null ? request.getActivo() : true)
                .build();

        return mapper.toResponse(transportadoraRepository.save(t));
    }

    @Override
    public TransportadoraResponse actualizar(Long id, TransportadoraRequest request) {
        Transportadora t = transportadoraRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Transportadora no encontrada con id: " + id));

        if (!t.getRucNit().equalsIgnoreCase(request.getRucNit()) && transportadoraRepository.existsByRucNit(request.getRucNit())) {
            throw new IllegalArgumentException("Ya existe otra transportadora con el RUC/NIT: " + request.getRucNit());
        }

        t.setNombre(request.getNombre().trim());
        t.setRucNit(request.getRucNit().trim());
        t.setTelefono(request.getTelefono());
        t.setEmail(request.getEmail());
        t.setContacto(request.getContacto());
        if (request.getTipoServicio() != null) t.setTipoServicio(request.getTipoServicio());
        if (request.getActivo() != null) t.setActivo(request.getActivo());

        return mapper.toResponse(transportadoraRepository.save(t));
    }

    @Override
    public void eliminar(Long id) {
        Transportadora t = transportadoraRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Transportadora no encontrada con id: " + id));
        t.setActivo(false);
        transportadoraRepository.save(t);
    }
}
