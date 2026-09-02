package com.proyecto.proyectoSpringBoot.service.impl;

import com.proyecto.proyectoSpringBoot.dto.request.ZonaBodegaRequest;
import com.proyecto.proyectoSpringBoot.dto.response.ZonaBodegaResponse;
import com.proyecto.proyectoSpringBoot.exception.ResourceNotFoundException;
import com.proyecto.proyectoSpringBoot.mapper.ZonaBodegaMapper;
import com.proyecto.proyectoSpringBoot.model.entity.Bodega;
import com.proyecto.proyectoSpringBoot.model.entity.ZonaBodega;
import com.proyecto.proyectoSpringBoot.model.enums.TipoZona;
import com.proyecto.proyectoSpringBoot.repository.BodegaRepository;
import com.proyecto.proyectoSpringBoot.repository.ZonaBodegaRepository;
import com.proyecto.proyectoSpringBoot.service.interfaces.IZonaBodegaService;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class ZonaBodegaServiceImpl implements IZonaBodegaService {

    private final ZonaBodegaRepository zonaBodegaRepository;
    private final BodegaRepository bodegaRepository;
    private final ZonaBodegaMapper mapper;

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "zonas", key = "'all'")
    public List<ZonaBodegaResponse> listarTodas() {
        return zonaBodegaRepository.findAll().stream()
                .map(mapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "zonas", key = "'bodega:' + #bodegaId")
    public List<ZonaBodegaResponse> listarPorBodega(Long bodegaId) {
        return zonaBodegaRepository.findByBodegaId(bodegaId).stream()
                .map(mapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ZonaBodegaResponse> listarPorTipo(TipoZona tipoZona) {
        return zonaBodegaRepository.findByTipoZona(tipoZona).stream()
                .map(mapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "zonas", key = "#id")
    public ZonaBodegaResponse obtenerPorId(Long id) {
        ZonaBodega z = zonaBodegaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Zona de bodega no encontrada con id: " + id));
        return mapper.toResponse(z);
    }

    @Override
    @CacheEvict(value = "zonas", allEntries = true)
    public ZonaBodegaResponse crear(ZonaBodegaRequest request) {
        Bodega bodega = bodegaRepository.findById(request.getBodegaId())
                .orElseThrow(() -> new ResourceNotFoundException("Bodega no encontrada con id: " + request.getBodegaId()));

        ZonaBodega z = ZonaBodega.builder()
                .codigo(request.getCodigo().trim().toUpperCase())
                .nombre(request.getNombre().trim())
                .tipoZona(request.getTipoZona() != null ? request.getTipoZona() : TipoZona.ALMACENAMIENTO)
                .bodega(bodega)
                .temperaturaControlada(request.getTemperaturaControlada() != null ? request.getTemperaturaControlada() : false)
                .descripcion(request.getDescripcion())
                .activo(request.getActivo() != null ? request.getActivo() : true)
                .build();

        return mapper.toResponse(zonaBodegaRepository.save(z));
    }

    @Override
    @CacheEvict(value = "zonas", allEntries = true)
    public ZonaBodegaResponse actualizar(Long id, ZonaBodegaRequest request) {
        ZonaBodega z = zonaBodegaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Zona de bodega no encontrada con id: " + id));

        if (request.getBodegaId() != null && !z.getBodega().getId().equals(request.getBodegaId())) {
            Bodega bodega = bodegaRepository.findById(request.getBodegaId())
                    .orElseThrow(() -> new ResourceNotFoundException("Bodega no encontrada con id: " + request.getBodegaId()));
            z.setBodega(bodega);
        }

        z.setCodigo(request.getCodigo().trim().toUpperCase());
        z.setNombre(request.getNombre().trim());
        if (request.getTipoZona() != null) z.setTipoZona(request.getTipoZona());
        if (request.getTemperaturaControlada() != null) z.setTemperaturaControlada(request.getTemperaturaControlada());
        z.setDescripcion(request.getDescripcion());
        if (request.getActivo() != null) z.setActivo(request.getActivo());

        return mapper.toResponse(zonaBodegaRepository.save(z));
    }

    @Override
    @CacheEvict(value = "zonas", allEntries = true)
    public void eliminar(Long id) {
        ZonaBodega z = zonaBodegaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Zona de bodega no encontrada con id: " + id));
        z.setActivo(false);
        zonaBodegaRepository.save(z);
    }
}
