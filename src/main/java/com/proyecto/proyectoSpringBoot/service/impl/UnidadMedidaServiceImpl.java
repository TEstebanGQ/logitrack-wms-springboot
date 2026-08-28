package com.proyecto.proyectoSpringBoot.service.impl;

import com.proyecto.proyectoSpringBoot.dto.request.UnidadMedidaRequest;
import com.proyecto.proyectoSpringBoot.dto.response.UnidadMedidaResponse;
import com.proyecto.proyectoSpringBoot.exception.ResourceNotFoundException;
import com.proyecto.proyectoSpringBoot.mapper.UnidadMedidaMapper;
import com.proyecto.proyectoSpringBoot.model.entity.UnidadMedida;
import com.proyecto.proyectoSpringBoot.repository.UnidadMedidaRepository;
import com.proyecto.proyectoSpringBoot.service.interfaces.IUnidadMedidaService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class UnidadMedidaServiceImpl implements IUnidadMedidaService {

    private final UnidadMedidaRepository unidadMedidaRepository;
    private final UnidadMedidaMapper mapper;

    @Override
    @Transactional(readOnly = true)
    public List<UnidadMedidaResponse> listarTodas() {
        return unidadMedidaRepository.findAll().stream()
                .map(mapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<UnidadMedidaResponse> listarActivas() {
        return unidadMedidaRepository.findByActivoTrue().stream()
                .map(mapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public UnidadMedidaResponse obtenerPorId(Long id) {
        UnidadMedida u = unidadMedidaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Unidad de medida no encontrada con id: " + id));
        return mapper.toResponse(u);
    }

    @Override
    public UnidadMedidaResponse crear(UnidadMedidaRequest request) {
        if (unidadMedidaRepository.existsByCodigo(request.getCodigo())) {
            throw new IllegalArgumentException("Ya existe una unidad de medida con el código: " + request.getCodigo());
        }

        UnidadMedida u = UnidadMedida.builder()
                .codigo(request.getCodigo().trim().toUpperCase())
                .nombre(request.getNombre().trim())
                .abreviatura(request.getAbreviatura())
                .factorConversion(request.getFactorConversion() != null ? request.getFactorConversion() : 1.0)
                .activo(request.getActivo() != null ? request.getActivo() : true)
                .build();

        return mapper.toResponse(unidadMedidaRepository.save(u));
    }

    @Override
    public UnidadMedidaResponse actualizar(Long id, UnidadMedidaRequest request) {
        UnidadMedida u = unidadMedidaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Unidad de medida no encontrada con id: " + id));

        if (!u.getCodigo().equalsIgnoreCase(request.getCodigo()) && unidadMedidaRepository.existsByCodigo(request.getCodigo())) {
            throw new IllegalArgumentException("Ya existe otra unidad de medida con el código: " + request.getCodigo());
        }

        u.setCodigo(request.getCodigo().trim().toUpperCase());
        u.setNombre(request.getNombre().trim());
        u.setAbreviatura(request.getAbreviatura());
        u.setFactorConversion(request.getFactorConversion());
        if (request.getActivo() != null) u.setActivo(request.getActivo());

        return mapper.toResponse(unidadMedidaRepository.save(u));
    }

    @Override
    public void eliminar(Long id) {
        UnidadMedida u = unidadMedidaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Unidad de medida no encontrada con id: " + id));
        u.setActivo(false);
        unidadMedidaRepository.save(u);
    }
}
