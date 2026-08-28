package com.proyecto.proyectoSpringBoot.service.impl;

import com.proyecto.proyectoSpringBoot.dto.request.TipoUbicacionRequest;
import com.proyecto.proyectoSpringBoot.dto.response.TipoUbicacionResponse;
import com.proyecto.proyectoSpringBoot.exception.ResourceNotFoundException;
import com.proyecto.proyectoSpringBoot.mapper.TipoUbicacionMapper;
import com.proyecto.proyectoSpringBoot.model.entity.TipoUbicacion;
import com.proyecto.proyectoSpringBoot.repository.TipoUbicacionRepository;
import com.proyecto.proyectoSpringBoot.service.interfaces.ITipoUbicacionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class TipoUbicacionServiceImpl implements ITipoUbicacionService {

    private final TipoUbicacionRepository tipoUbicacionRepository;
    private final TipoUbicacionMapper mapper;

    @Override
    @Transactional(readOnly = true)
    public List<TipoUbicacionResponse> listarTodas() {
        return tipoUbicacionRepository.findAll().stream()
                .map(mapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<TipoUbicacionResponse> listarActivas() {
        return tipoUbicacionRepository.findByActivoTrue().stream()
                .map(mapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public TipoUbicacionResponse obtenerPorId(Long id) {
        TipoUbicacion t = tipoUbicacionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Tipo de ubicación no encontrado con id: " + id));
        return mapper.toResponse(t);
    }

    @Override
    public TipoUbicacionResponse crear(TipoUbicacionRequest request) {
        if (tipoUbicacionRepository.existsByCodigo(request.getCodigo())) {
            throw new IllegalArgumentException("Ya existe un tipo de ubicación con el código: " + request.getCodigo());
        }

        TipoUbicacion t = TipoUbicacion.builder()
                .codigo(request.getCodigo().trim().toUpperCase())
                .nombre(request.getNombre().trim())
                .pesoMaximoKg(request.getPesoMaximoKg() != null ? request.getPesoMaximoKg() : 1000.0)
                .volumenMaximoM3(request.getVolumenMaximoM3() != null ? request.getVolumenMaximoM3() : 2.5)
                .descripcion(request.getDescripcion())
                .activo(request.getActivo() != null ? request.getActivo() : true)
                .build();

        return mapper.toResponse(tipoUbicacionRepository.save(t));
    }

    @Override
    public TipoUbicacionResponse actualizar(Long id, TipoUbicacionRequest request) {
        TipoUbicacion t = tipoUbicacionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Tipo de ubicación no encontrado con id: " + id));

        if (!t.getCodigo().equalsIgnoreCase(request.getCodigo()) && tipoUbicacionRepository.existsByCodigo(request.getCodigo())) {
            throw new IllegalArgumentException("Ya existe otro tipo de ubicación con el código: " + request.getCodigo());
        }

        t.setCodigo(request.getCodigo().trim().toUpperCase());
        t.setNombre(request.getNombre().trim());
        t.setPesoMaximoKg(request.getPesoMaximoKg());
        t.setVolumenMaximoM3(request.getVolumenMaximoM3());
        t.setDescripcion(request.getDescripcion());
        if (request.getActivo() != null) t.setActivo(request.getActivo());

        return mapper.toResponse(tipoUbicacionRepository.save(t));
    }

    @Override
    public void eliminar(Long id) {
        TipoUbicacion t = tipoUbicacionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Tipo de ubicación no encontrado con id: " + id));
        t.setActivo(false);
        tipoUbicacionRepository.save(t);
    }
}
