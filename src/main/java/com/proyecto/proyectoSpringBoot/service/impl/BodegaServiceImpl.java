package com.proyecto.proyectoSpringBoot.service.impl;

import com.proyecto.proyectoSpringBoot.dto.request.CrearBodegaRequest;
import com.proyecto.proyectoSpringBoot.dto.response.BodegaResponse;
import com.proyecto.proyectoSpringBoot.exception.ResourceNotFoundException;
import com.proyecto.proyectoSpringBoot.mapper.BodegaMapper;
import com.proyecto.proyectoSpringBoot.model.entity.Bodega;
import com.proyecto.proyectoSpringBoot.repository.BodegaRepository;
import com.proyecto.proyectoSpringBoot.service.interfaces.IBodegaService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class BodegaServiceImpl implements IBodegaService {

    private final BodegaRepository bodegaRepository;
    private final BodegaMapper bodegaMapper;

    @Override
    public BodegaResponse crear(CrearBodegaRequest request) {
        Bodega bodega = bodegaMapper.toEntity(request);
        return bodegaMapper.toResponse(bodegaRepository.save(bodega));
    }

    @Override
    @Transactional(readOnly = true)
    public BodegaResponse obtenerPorId(Long id) {
        return bodegaMapper.toResponse(findById(id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<BodegaResponse> listarTodas() {
        return bodegaRepository.findAll().stream()
                .map(bodegaMapper::toResponse).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<BodegaResponse> listarActivas() {
        return bodegaRepository.findByActivoTrue().stream()
                .map(bodegaMapper::toResponse).collect(Collectors.toList());
    }

    @Override
    public BodegaResponse actualizar(Long id, CrearBodegaRequest request) {
        Bodega bodega = findById(id);
        bodegaMapper.updateEntity(bodega, request);
        return bodegaMapper.toResponse(bodegaRepository.save(bodega));
    }

    @Override
    public void eliminar(Long id) {
        Bodega bodega = findById(id);
        bodega.setActivo(false);
        bodegaRepository.save(bodega);
    }

    private Bodega findById(Long id) {
        return bodegaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Bodega no encontrada con id: " + id));
    }
}
