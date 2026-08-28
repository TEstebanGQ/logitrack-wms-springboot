package com.proyecto.proyectoSpringBoot.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.proyecto.proyectoSpringBoot.dto.request.CrearUbicacionRequest;
import com.proyecto.proyectoSpringBoot.dto.response.UbicacionBodegaResponse;
import com.proyecto.proyectoSpringBoot.event.AuditoriaEvent;
import com.proyecto.proyectoSpringBoot.exception.ResourceNotFoundException;
import com.proyecto.proyectoSpringBoot.mapper.UbicacionBodegaMapper;
import com.proyecto.proyectoSpringBoot.model.entity.Bodega;
import com.proyecto.proyectoSpringBoot.model.entity.UbicacionBodega;
import com.proyecto.proyectoSpringBoot.model.enums.TipoOperacion;
import com.proyecto.proyectoSpringBoot.repository.BodegaRepository;
import com.proyecto.proyectoSpringBoot.repository.UbicacionBodegaRepository;
import com.proyecto.proyectoSpringBoot.service.interfaces.IUbicacionBodegaService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class UbicacionBodegaServiceImpl implements IUbicacionBodegaService {

    private final UbicacionBodegaRepository ubicacionRepository;
    private final BodegaRepository bodegaRepository;
    private final UbicacionBodegaMapper mapper;
    private final ApplicationEventPublisher eventPublisher;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public UbicacionBodegaResponse crearUbicacion(CrearUbicacionRequest request, String emailUsuario) {
        Bodega bodega = bodegaRepository.findById(request.getBodegaId())
                .orElseThrow(() -> new ResourceNotFoundException("Bodega no encontrada: " + request.getBodegaId()));

        if (ubicacionRepository.existsByBodegaIdAndCodigoUbicacion(bodega.getId(), request.getCodigoUbicacion())) {
            throw new RuntimeException("Ya existe una posición con el código '" + request.getCodigoUbicacion() + "' en la bodega '" + bodega.getNombre() + "'");
        }

        UbicacionBodega ubicacion = UbicacionBodega.builder()
                .bodega(bodega)
                .codigoUbicacion(request.getCodigoUbicacion())
                .pasillo(request.getPasillo())
                .estante(request.getEstante())
                .nivel(request.getNivel())
                .capacidadMax(request.getCapacidadMax() != null ? request.getCapacidadMax() : 100)
                .descripcion(request.getDescripcion())
                .activo(true)
                .createdAt(LocalDateTime.now())
                .build();

        UbicacionBodega guardada = ubicacionRepository.save(ubicacion);
        UbicacionBodegaResponse resp = mapper.toResponse(guardada);

        publishAudit("UbicacionBodega", guardada.getId(), TipoOperacion.INSERT, null, toJson(resp),
                "Creó ubicación física '" + guardada.getCodigoUbicacion() + "' en " + bodega.getNombre(), emailUsuario);

        return resp;
    }

    @Override
    @Transactional(readOnly = true)
    public List<UbicacionBodegaResponse> listarTodas() {
        return ubicacionRepository.findAll().stream()
                .map(mapper::toResponse).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<UbicacionBodegaResponse> listarPorBodega(Long bodegaId, boolean soloActivos) {
        List<UbicacionBodega> lista = soloActivos ?
                ubicacionRepository.findByBodegaIdAndActivoTrue(bodegaId) :
                ubicacionRepository.findByBodegaId(bodegaId);
        return lista.stream().map(mapper::toResponse).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public UbicacionBodegaResponse obtenerPorId(Long id) {
        return mapper.toResponse(ubicacionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Ubicación no encontrada: " + id)));
    }

    @Override
    public void desactivarUbicacion(Long id, String emailUsuario) {
        UbicacionBodega ubicacion = ubicacionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Ubicación no encontrada: " + id));

        String valAnt = toJson(mapper.toResponse(ubicacion));
        ubicacion.setActivo(false);
        UbicacionBodega guardada = ubicacionRepository.save(ubicacion);

        publishAudit("UbicacionBodega", guardada.getId(), TipoOperacion.DELETE, valAnt, toJson(mapper.toResponse(guardada)),
                "Desactivó ubicación '" + guardada.getCodigoUbicacion() + "'", emailUsuario);
    }

    private void publishAudit(String entidad, Long entidadId, TipoOperacion tipo, String ant, String nuevos, String desc, String email) {
        try {
            eventPublisher.publishEvent(AuditoriaEvent.builder()
                    .entidad(entidad)
                    .entidadId(entidadId)
                    .tipoOperacion(tipo)
                    .emailUsuario(email)
                    .valoresAnteriores(ant)
                    .valoresNuevos(nuevos)
                    .descripcion(desc)
                    .build());
        } catch (Exception ignored) {}
    }

    private String toJson(Object obj) {
        try { return objectMapper.writeValueAsString(obj); } catch (Exception e) { return null; }
    }
}
