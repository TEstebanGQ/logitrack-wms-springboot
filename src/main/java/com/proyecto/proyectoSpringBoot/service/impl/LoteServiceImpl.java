package com.proyecto.proyectoSpringBoot.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.proyecto.proyectoSpringBoot.dto.request.CrearLoteRequest;
import com.proyecto.proyectoSpringBoot.dto.response.LoteResponse;
import com.proyecto.proyectoSpringBoot.event.AuditoriaEvent;
import com.proyecto.proyectoSpringBoot.exception.ResourceNotFoundException;
import com.proyecto.proyectoSpringBoot.mapper.LoteMapper;
import com.proyecto.proyectoSpringBoot.model.entity.Bodega;
import com.proyecto.proyectoSpringBoot.model.entity.Lote;
import com.proyecto.proyectoSpringBoot.model.entity.Producto;
import com.proyecto.proyectoSpringBoot.model.enums.EstadoLote;
import com.proyecto.proyectoSpringBoot.model.enums.TipoOperacion;
import com.proyecto.proyectoSpringBoot.repository.BodegaRepository;
import com.proyecto.proyectoSpringBoot.repository.LoteRepository;
import com.proyecto.proyectoSpringBoot.repository.ProductoRepository;
import com.proyecto.proyectoSpringBoot.service.interfaces.ILoteService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class LoteServiceImpl implements ILoteService {

    private final LoteRepository loteRepository;
    private final ProductoRepository productoRepository;
    private final BodegaRepository bodegaRepository;
    private final LoteMapper mapper;
    private final ApplicationEventPublisher eventPublisher;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public LoteResponse crearLote(CrearLoteRequest request, String emailUsuario) {
        if (loteRepository.existsByCodigoLoteAndProductoIdAndBodegaId(
                request.getCodigoLote(), request.getProductoId(), request.getBodegaId())) {
            throw new RuntimeException("Ya existe un lote con el código '" + request.getCodigoLote() + "' para este producto en la bodega indicada");
        }

        Producto producto = productoRepository.findById(request.getProductoId())
                .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado: " + request.getProductoId()));

        Bodega bodega = bodegaRepository.findById(request.getBodegaId())
                .orElseThrow(() -> new ResourceNotFoundException("Bodega no encontrada: " + request.getBodegaId()));

        Lote lote = Lote.builder()
                .codigoLote(request.getCodigoLote())
                .producto(producto)
                .bodega(bodega)
                .stockInicial(request.getStockInicial())
                .stockActual(request.getStockInicial())
                .fechaFabricacion(request.getFechaFabricacion())
                .fechaVencimiento(request.getFechaVencimiento())
                .estado(request.getEstado() != null ? request.getEstado() : EstadoLote.DISPONIBLE)
                .createdAt(LocalDateTime.now())
                .build();

        Lote guardado = loteRepository.save(lote);
        LoteResponse resp = mapper.toResponse(guardado);

        publishAudit("Lote", guardado.getId(), TipoOperacion.INSERT, null, toJson(resp),
                "Creó Lote '" + guardado.getCodigoLote() + "' para " + producto.getNombre() + " en " + bodega.getNombre() + " (Cant: " + guardado.getStockInicial() + ")", emailUsuario);

        return resp;
    }

    @Override
    @Transactional(readOnly = true)
    public List<LoteResponse> listarTodos() {
        return loteRepository.findAll().stream()
                .map(mapper::toResponse).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<LoteResponse> listarPorProducto(Long productoId) {
        return loteRepository.findByProductoId(productoId).stream()
                .map(mapper::toResponse).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<LoteResponse> listarPorBodega(Long bodegaId) {
        return loteRepository.findByBodegaId(bodegaId).stream()
                .map(mapper::toResponse).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<LoteResponse> listarPorProductoYBodegaFEFO(Long productoId, Long bodegaId) {
        return loteRepository.findByProductoIdAndBodegaIdOrderByFechaVencimientoAsc(productoId, bodegaId).stream()
                .map(mapper::toResponse).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<LoteResponse> listarProximosAVencer(int dias) {
        LocalDate hoy = LocalDate.now();
        LocalDate limite = hoy.plusDays(dias);
        return loteRepository.findByFechaVencimientoBetween(hoy, limite).stream()
                .map(mapper::toResponse).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public LoteResponse obtenerPorId(Long id) {
        return mapper.toResponse(loteRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Lote no encontrado: " + id)));
    }

    @Override
    public LoteResponse actualizarEstado(Long id, EstadoLote estado, String emailUsuario) {
        Lote lote = loteRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Lote no encontrado: " + id));

        String valAnt = toJson(mapper.toResponse(lote));
        lote.setEstado(estado);
        Lote guardado = loteRepository.save(lote);
        LoteResponse resp = mapper.toResponse(guardado);

        publishAudit("Lote", guardado.getId(), TipoOperacion.UPDATE, valAnt, toJson(resp),
                "Cambió estado de Lote " + guardado.getCodigoLote() + " a " + estado, emailUsuario);

        return resp;
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
