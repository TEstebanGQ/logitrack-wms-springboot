package com.proyecto.proyectoSpringBoot.service.impl;

import com.proyecto.proyectoSpringBoot.dto.request.CrearCategoriaRequest;
import com.proyecto.proyectoSpringBoot.dto.response.CategoriaResponse;
import com.proyecto.proyectoSpringBoot.exception.ResourceNotFoundException;
import com.proyecto.proyectoSpringBoot.mapper.CategoriaMapper;
import com.proyecto.proyectoSpringBoot.model.entity.Categoria;
import com.proyecto.proyectoSpringBoot.repository.CategoriaRepository;
import com.proyecto.proyectoSpringBoot.service.interfaces.ICategoriaService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CategoriaServiceImpl implements ICategoriaService {
    private final CategoriaRepository repository;
    private final CategoriaMapper mapper;

    @Override
    @Transactional
    public CategoriaResponse crear(CrearCategoriaRequest request) {
        if (repository.existsByNombreIgnoreCase(request.getNombre())) {
            throw new RuntimeException("Ya existe una categoría con este nombre");
        }
        Categoria c = mapper.toEntity(request);
        return mapper.toResponse(repository.save(c));
    }

    @Override
    @Transactional(readOnly = true)
    public List<CategoriaResponse> listar() {
        return repository.findByActivoTrue().stream().map(mapper::toResponse).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public CategoriaResponse obtenerPorId(Long id) {
        return mapper.toResponse(repository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Categoría no encontrada")));
    }

    @Override
    @Transactional
    public CategoriaResponse actualizar(Long id, CrearCategoriaRequest request) {
        Categoria c = repository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Categoría no encontrada"));
        c.setNombre(request.getNombre());
        c.setDescripcion(request.getDescripcion());
        return mapper.toResponse(repository.save(c));
    }

    @Override
    @Transactional
    public void eliminar(Long id) {
        Categoria c = repository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Categoría no encontrada"));
        c.setActivo(false);
        repository.save(c);
    }
}
