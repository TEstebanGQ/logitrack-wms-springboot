package com.proyecto.proyectoSpringBoot.service.impl;

import com.proyecto.proyectoSpringBoot.dto.request.CrearCategoriaRequest;
import com.proyecto.proyectoSpringBoot.dto.response.CategoriaResponse;
import com.proyecto.proyectoSpringBoot.exception.ResourceNotFoundException;
import com.proyecto.proyectoSpringBoot.listener.AuditoriaHelper;
import com.proyecto.proyectoSpringBoot.mapper.CategoriaMapper;
import com.proyecto.proyectoSpringBoot.model.entity.Categoria;
import com.proyecto.proyectoSpringBoot.model.enums.TipoOperacion;
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
    private final AuditoriaHelper auditoriaHelper;

    @Override
    @Transactional
    public CategoriaResponse crear(CrearCategoriaRequest request) {
        if (repository.existsByNombreIgnoreCase(request.getNombre())) {
            throw new RuntimeException("Ya existe una categoría con este nombre");
        }
        Categoria c = mapper.toEntity(request);
        Categoria guardada = repository.save(c);
        CategoriaResponse resp = mapper.toResponse(guardada);

        auditoriaHelper.publishAudit("Categoría", guardada.getId(), TipoOperacion.INSERT, null, auditoriaHelper.toJson(resp),
                "Creó categoría '" + guardada.getNombre() + "' (" + (guardada.getDescripcion() != null ? guardada.getDescripcion() : "Sin descripción") + ")");

        return resp;
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
        String valoresAnt = auditoriaHelper.toJson(mapper.toResponse(c));
        c.setNombre(request.getNombre());
        c.setDescripcion(request.getDescripcion());
        Categoria guardada = repository.save(c);
        CategoriaResponse resp = mapper.toResponse(guardada);

        auditoriaHelper.publishAudit("Categoría", guardada.getId(), TipoOperacion.UPDATE, valoresAnt, auditoriaHelper.toJson(resp),
                "Actualizó categoría '" + guardada.getNombre() + "'");

        return resp;
    }

    @Override
    @Transactional
    public void eliminar(Long id) {
        Categoria c = repository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Categoría no encontrada"));
        String valoresAnt = auditoriaHelper.toJson(mapper.toResponse(c));
        c.setActivo(false);
        Categoria guardada = repository.save(c);

        auditoriaHelper.publishAudit("Categoría", guardada.getId(), TipoOperacion.DELETE, valoresAnt, auditoriaHelper.toJson(mapper.toResponse(guardada)),
                "Desactivó categoría '" + guardada.getNombre() + "'");
    }
}
