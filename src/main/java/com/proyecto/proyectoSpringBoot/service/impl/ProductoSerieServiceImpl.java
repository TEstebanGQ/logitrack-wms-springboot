package com.proyecto.proyectoSpringBoot.service.impl;

import com.proyecto.proyectoSpringBoot.dto.request.ProductoSerieRequest;
import com.proyecto.proyectoSpringBoot.dto.response.ProductoSerieResponse;
import com.proyecto.proyectoSpringBoot.exception.ResourceNotFoundException;
import com.proyecto.proyectoSpringBoot.mapper.ProductoSerieMapper;
import com.proyecto.proyectoSpringBoot.model.entity.Bodega;
import com.proyecto.proyectoSpringBoot.model.entity.Producto;
import com.proyecto.proyectoSpringBoot.model.entity.ProductoSerie;
import com.proyecto.proyectoSpringBoot.model.entity.UbicacionBodega;
import com.proyecto.proyectoSpringBoot.model.enums.EstadoSerie;
import com.proyecto.proyectoSpringBoot.repository.BodegaRepository;
import com.proyecto.proyectoSpringBoot.repository.ProductoRepository;
import com.proyecto.proyectoSpringBoot.repository.ProductoSerieRepository;
import com.proyecto.proyectoSpringBoot.repository.UbicacionBodegaRepository;
import com.proyecto.proyectoSpringBoot.service.interfaces.IProductoSerieService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class ProductoSerieServiceImpl implements IProductoSerieService {

    private final ProductoSerieRepository productoSerieRepository;
    private final ProductoRepository productoRepository;
    private final BodegaRepository bodegaRepository;
    private final UbicacionBodegaRepository ubicacionBodegaRepository;
    private final ProductoSerieMapper mapper;

    @Override
    @Transactional(readOnly = true)
    public List<ProductoSerieResponse> listarTodas() {
        return productoSerieRepository.findAll().stream()
                .map(mapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductoSerieResponse> listarPorProducto(Long productoId) {
        return productoSerieRepository.findByProductoId(productoId).stream()
                .map(mapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductoSerieResponse> listarPorBodega(Long bodegaId) {
        return productoSerieRepository.findByBodegaId(bodegaId).stream()
                .map(mapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductoSerieResponse> listarPorEstado(EstadoSerie estado) {
        return productoSerieRepository.findByEstado(estado).stream()
                .map(mapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public ProductoSerieResponse obtenerPorId(Long id) {
        ProductoSerie s = productoSerieRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Número de serie no encontrado con id: " + id));
        return mapper.toResponse(s);
    }

    @Override
    public ProductoSerieResponse registrar(ProductoSerieRequest request) {
        if (productoSerieRepository.existsByNumeroSerie(request.getNumeroSerie())) {
            throw new IllegalArgumentException("Ya existe el número de serie: " + request.getNumeroSerie());
        }

        Producto producto = productoRepository.findById(request.getProductoId())
                .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado con id: " + request.getProductoId()));

        Bodega bodega = null;
        if (request.getBodegaId() != null) {
            bodega = bodegaRepository.findById(request.getBodegaId())
                    .orElseThrow(() -> new ResourceNotFoundException("Bodega no encontrada con id: " + request.getBodegaId()));
        }

        UbicacionBodega ubicacion = null;
        if (request.getUbicacionId() != null) {
            ubicacion = ubicacionBodegaRepository.findById(request.getUbicacionId())
                    .orElseThrow(() -> new ResourceNotFoundException("Ubicación no encontrada con id: " + request.getUbicacionId()));
        }

        ProductoSerie s = ProductoSerie.builder()
                .numeroSerie(request.getNumeroSerie().trim())
                .producto(producto)
                .bodega(bodega)
                .ubicacion(ubicacion)
                .estado(request.getEstado() != null ? request.getEstado() : EstadoSerie.EN_STOCK)
                .observaciones(request.getObservaciones())
                .build();

        return mapper.toResponse(productoSerieRepository.save(s));
    }

    @Override
    public ProductoSerieResponse actualizarEstado(Long id, EstadoSerie nuevoEstado, String observaciones) {
        ProductoSerie s = productoSerieRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Número de serie no encontrado con id: " + id));

        s.setEstado(nuevoEstado);
        if (nuevoEstado == EstadoSerie.DESPACHADO) {
            s.setFechaDespacho(LocalDateTime.now());
        }
        if (observaciones != null && !observaciones.isBlank()) {
            s.setObservaciones(observaciones);
        }

        return mapper.toResponse(productoSerieRepository.save(s));
    }

    @Override
    public void eliminar(Long id) {
        ProductoSerie s = productoSerieRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Número de serie no encontrado con id: " + id));
        s.setEstado(EstadoSerie.DE_BAJA);
        productoSerieRepository.save(s);
    }
}
