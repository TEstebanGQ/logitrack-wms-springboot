package com.proyecto.proyectoSpringBoot.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.proyecto.proyectoSpringBoot.dto.request.CrearBodegaRequest;
import com.proyecto.proyectoSpringBoot.dto.response.BodegaResponse;
import com.proyecto.proyectoSpringBoot.dto.response.InventarioBodegaResponse;
import com.proyecto.proyectoSpringBoot.event.AuditoriaEvent;
import com.proyecto.proyectoSpringBoot.exception.ResourceNotFoundException;
import com.proyecto.proyectoSpringBoot.mapper.BodegaMapper;
import com.proyecto.proyectoSpringBoot.model.entity.Bodega;
import com.proyecto.proyectoSpringBoot.model.enums.TipoOperacion;
import com.proyecto.proyectoSpringBoot.repository.BodegaRepository;
import com.proyecto.proyectoSpringBoot.repository.InventarioBodegaRepository;
import com.proyecto.proyectoSpringBoot.service.interfaces.IBodegaService;
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
@Transactional
public class BodegaServiceImpl implements IBodegaService {

    private final BodegaRepository bodegaRepository;
    private final InventarioBodegaRepository inventarioRepository;
    private final BodegaMapper bodegaMapper;
    private final ApplicationEventPublisher eventPublisher;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public BodegaResponse crear(CrearBodegaRequest request) {
        Bodega bodega = bodegaMapper.toEntity(request);
        Bodega guardada = bodegaRepository.save(bodega);
        BodegaResponse resp = bodegaMapper.toResponse(guardada);

        publishAudit("Bodega", guardada.getId(), TipoOperacion.INSERT, null, toJson(resp),
                "Creó bodega '" + guardada.getNombre() + "' en " + guardada.getUbicacion() + " (Capacidad: " + guardada.getCapacidad() + " u.)");

        return resp;
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
    @Transactional(readOnly = true)
    public List<InventarioBodegaResponse> obtenerInventarioPorBodega(Long bodegaId) {
        Bodega bodega = findById(bodegaId);
        return inventarioRepository.findByBodegaId(bodega.getId()).stream()
                .map(inv -> InventarioBodegaResponse.builder()
                        .bodegaId(bodega.getId())
                        .bodegaNombre(bodega.getNombre())
                        .productoId(inv.getProducto().getId())
                        .productoNombre(inv.getProducto().getNombre())
                        .stockActual(inv.getStockActual())
                        .build())
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<InventarioBodegaResponse> obtenerInventarioPorProducto(Long productoId) {
        return inventarioRepository.findByProductoId(productoId).stream()
                .map(inv -> InventarioBodegaResponse.builder()
                        .bodegaId(inv.getBodega().getId())
                        .bodegaNombre(inv.getBodega().getNombre())
                        .productoId(inv.getProducto().getId())
                        .productoNombre(inv.getProducto().getNombre())
                        .stockActual(inv.getStockActual())
                        .build())
                .collect(Collectors.toList());
    }

    @Override
    public BodegaResponse actualizar(Long id, CrearBodegaRequest request) {
        Bodega bodega = findById(id);
        String valoresAnt = toJson(bodegaMapper.toResponse(bodega));
        bodegaMapper.updateEntity(bodega, request);
        Bodega actualizada = bodegaRepository.save(bodega);
        BodegaResponse resp = bodegaMapper.toResponse(actualizada);

        publishAudit("Bodega", actualizada.getId(), TipoOperacion.UPDATE, valoresAnt, toJson(resp),
                "Actualizó bodega '" + actualizada.getNombre() + "'. Capacidad: " + actualizada.getCapacidad() + " u., Ubicación: " + actualizada.getUbicacion());

        return resp;
    }

    @Override
    public void eliminar(Long id) {
        Bodega bodega = findById(id);
        String valoresAnt = toJson(bodegaMapper.toResponse(bodega));
        bodega.setActivo(false);
        Bodega guardada = bodegaRepository.save(bodega);

        publishAudit("Bodega", guardada.getId(), TipoOperacion.DELETE, valoresAnt, toJson(bodegaMapper.toResponse(guardada)),
                "Desactivó / eliminó la bodega '" + guardada.getNombre() + "'");
    }

    private Bodega findById(Long id) {
        return bodegaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Bodega no encontrada con id: " + id));
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
