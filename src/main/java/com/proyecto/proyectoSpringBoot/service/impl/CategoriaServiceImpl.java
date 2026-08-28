package com.proyecto.proyectoSpringBoot.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.proyecto.proyectoSpringBoot.dto.request.CrearCategoriaRequest;
import com.proyecto.proyectoSpringBoot.dto.response.CategoriaResponse;
import com.proyecto.proyectoSpringBoot.event.AuditoriaEvent;
import com.proyecto.proyectoSpringBoot.exception.ResourceNotFoundException;
import com.proyecto.proyectoSpringBoot.mapper.CategoriaMapper;
import com.proyecto.proyectoSpringBoot.model.entity.Categoria;
import com.proyecto.proyectoSpringBoot.model.enums.TipoOperacion;
import com.proyecto.proyectoSpringBoot.repository.CategoriaRepository;
import com.proyecto.proyectoSpringBoot.service.interfaces.ICategoriaService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CategoriaServiceImpl implements ICategoriaService {
    private final CategoriaRepository repository;
    private final CategoriaMapper mapper;
    private final ApplicationEventPublisher eventPublisher;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    @Transactional
    public CategoriaResponse crear(CrearCategoriaRequest request) {
        if (repository.existsByNombreIgnoreCase(request.getNombre())) {
            throw new RuntimeException("Ya existe una categoría con este nombre");
        }
        Categoria c = mapper.toEntity(request);
        Categoria guardada = repository.save(c);
        CategoriaResponse resp = mapper.toResponse(guardada);

        publishAudit("Categoría", guardada.getId(), TipoOperacion.INSERT, null, toJson(resp),
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
        String valoresAnt = toJson(mapper.toResponse(c));
        c.setNombre(request.getNombre());
        c.setDescripcion(request.getDescripcion());
        Categoria guardada = repository.save(c);
        CategoriaResponse resp = mapper.toResponse(guardada);

        publishAudit("Categoría", guardada.getId(), TipoOperacion.UPDATE, valoresAnt, toJson(resp),
                "Actualizó categoría '" + guardada.getNombre() + "'");

        return resp;
    }

    @Override
    @Transactional
    public void eliminar(Long id) {
        Categoria c = repository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Categoría no encontrada"));
        String valoresAnt = toJson(mapper.toResponse(c));
        c.setActivo(false);
        Categoria guardada = repository.save(c);

        publishAudit("Categoría", guardada.getId(), TipoOperacion.DELETE, valoresAnt, toJson(mapper.toResponse(guardada)),
                "Desactivó categoría '" + guardada.getNombre() + "'");
    }

    private void publishAudit(String entidad, Long entidadId, TipoOperacion tipo, String ant, String nuevos, String desc) {
        try {
            eventPublisher.publishEvent(AuditoriaEvent.builder()
                    .entidad(entidad)
                    .entidadId(entidadId)
                    .tipoOperacion(tipo)
                    .emailUsuario(getCurrentUserEmail())
                    .valoresAnteriores(ant)
                    .valoresNuevos(nuevos)
                    .descripcion(desc)
                    .build());
        } catch (Exception ignored) {}
    }

    private String getCurrentUserEmail() {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth != null && auth.isAuthenticated() && !auth.getPrincipal().equals("anonymousUser")) {
                return auth.getName();
            }
        } catch (Exception ignored) {}
        return "admin@logitrack.com";
    }

    private String toJson(Object obj) {
        try { return objectMapper.writeValueAsString(obj); } catch (Exception e) { return null; }
    }
}
