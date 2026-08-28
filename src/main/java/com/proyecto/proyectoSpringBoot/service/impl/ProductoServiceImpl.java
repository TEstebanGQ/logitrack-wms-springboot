package com.proyecto.proyectoSpringBoot.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.proyecto.proyectoSpringBoot.dto.request.CrearProductoRequest;
import com.proyecto.proyectoSpringBoot.dto.response.ProductoResponse;
import com.proyecto.proyectoSpringBoot.event.AuditoriaEvent;
import com.proyecto.proyectoSpringBoot.exception.ResourceNotFoundException;
import com.proyecto.proyectoSpringBoot.mapper.ProductoMapper;
import com.proyecto.proyectoSpringBoot.model.entity.Bodega;
import com.proyecto.proyectoSpringBoot.model.entity.Categoria;
import com.proyecto.proyectoSpringBoot.model.entity.InventarioBodega;
import com.proyecto.proyectoSpringBoot.model.entity.Producto;
import com.proyecto.proyectoSpringBoot.model.enums.TipoOperacion;
import com.proyecto.proyectoSpringBoot.repository.BodegaRepository;
import com.proyecto.proyectoSpringBoot.repository.CategoriaRepository;
import com.proyecto.proyectoSpringBoot.repository.InventarioBodegaRepository;
import com.proyecto.proyectoSpringBoot.repository.ProductoRepository;
import com.proyecto.proyectoSpringBoot.service.interfaces.IProductoService;
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
public class ProductoServiceImpl implements IProductoService {

    private final ProductoRepository productoRepository;
    private final ProductoMapper productoMapper;
    private final CategoriaRepository categoriaRepository;
    private final BodegaRepository bodegaRepository;
    private final InventarioBodegaRepository inventarioRepository;
    private final ApplicationEventPublisher eventPublisher;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public ProductoResponse crear(CrearProductoRequest request) {
        Categoria categoria = categoriaRepository.findById(request.getCategoriaId())
            .orElseThrow(() -> new ResourceNotFoundException("Categoría no encontrada"));
        Producto producto = productoMapper.toEntity(request);
        producto.setCategoria(categoria);
        Producto guardado = productoRepository.save(producto);
        ProductoResponse resp = productoMapper.toResponse(guardado);

        // Asignar el stock inicial a la bodega elegida (o a la primera bodega activa)
        if (guardado.getStock() > 0) {
            Bodega targetBodega = null;
            if (request.getBodegaId() != null) {
                targetBodega = bodegaRepository.findById(request.getBodegaId()).orElse(null);
            }
            if (targetBodega == null) {
                targetBodega = bodegaRepository.findByActivoTrue().stream().findFirst().orElse(null);
            }
            if (targetBodega != null) {
                inventarioRepository.save(InventarioBodega.builder()
                        .bodega(targetBodega)
                        .producto(guardado)
                        .stockActual(guardado.getStock())
                        .build());
            }
        }

        publishAudit("Producto", guardado.getId(), TipoOperacion.INSERT, null, toJson(resp),
                "Creó producto '" + guardado.getNombre() + "' ($" + guardado.getPrecio() + ", Stock Inicial: " + guardado.getStock() + " u.)");

        return resp;
    }

    @Override
    @Transactional(readOnly = true)
    public ProductoResponse obtenerPorId(Long id) {
        return productoMapper.toResponse(findById(id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductoResponse> listarTodos() {
        return productoRepository.findAll().stream()
                .map(productoMapper::toResponse).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductoResponse> listarActivos() {
        return productoRepository.findByActivoTrue().stream()
                .map(productoMapper::toResponse).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductoResponse> listarConStockBajo(int umbral) {
        return productoRepository.findByStockLessThan(umbral).stream()
                .map(productoMapper::toResponse).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductoResponse> listarPorCategoria(String categoria) {
        return productoRepository.findByCategoriaNombreIgnoreCase(categoria).stream()
                .map(productoMapper::toResponse).collect(Collectors.toList());
    }

    @Override
    public ProductoResponse actualizar(Long id, CrearProductoRequest request) {
        Producto producto = findById(id);
        int oldStock = producto.getStock() != null ? producto.getStock() : 0;
        int newStock = request.getStock() != null ? request.getStock() : 0;
        int delta = newStock - oldStock;

        String valoresAnt = toJson(productoMapper.toResponse(producto));
        Categoria categoria = categoriaRepository.findById(request.getCategoriaId())
            .orElseThrow(() -> new ResourceNotFoundException("Categoría no encontrada"));
        productoMapper.updateEntity(producto, request);
        producto.setCategoria(categoria);
        Producto actualizado = productoRepository.save(producto);

        // Sincronizar la diferencia de stock (delta) en la bodega explícitamente seleccionada
        if (delta != 0) {
            Bodega targetBodega = null;
            if (request.getBodegaId() != null) {
                targetBodega = bodegaRepository.findById(request.getBodegaId()).orElse(null);
            }
            if (targetBodega == null) {
                targetBodega = inventarioRepository.findByProductoId(actualizado.getId()).stream()
                        .map(InventarioBodega::getBodega)
                        .filter(Bodega::isActivo)
                        .findFirst()
                        .orElse(null);
            }
            if (targetBodega == null) {
                targetBodega = bodegaRepository.findByActivoTrue().stream().findFirst().orElse(null);
            }

            if (targetBodega != null) {
                final Bodega finalBodega = targetBodega;
                InventarioBodega inv = inventarioRepository
                        .findByBodegaIdAndProductoId(finalBodega.getId(), actualizado.getId())
                        .orElseGet(() -> InventarioBodega.builder()
                                .bodega(finalBodega)
                                .producto(actualizado)
                                .stockActual(0)
                                .build());
                inv.setStockActual(Math.max(0, inv.getStockActual() + delta));
                inventarioRepository.save(inv);
            }
        }

        ProductoResponse resp = productoMapper.toResponse(actualizado);

        publishAudit("Producto", actualizado.getId(), TipoOperacion.UPDATE, valoresAnt, toJson(resp),
                "Actualizó producto '" + actualizado.getNombre() + "' ($" + actualizado.getPrecio() + ", Stock: " + actualizado.getStock() + " u., Ajuste: " + (delta >= 0 ? "+" : "") + delta + " u.)");

        return resp;
    }

    @Override
    public void eliminar(Long id) {
        Producto producto = findById(id);
        String valoresAnt = toJson(productoMapper.toResponse(producto));
        producto.setActivo(false);
        Producto guardado = productoRepository.save(producto);

        publishAudit("Producto", guardado.getId(), TipoOperacion.DELETE, valoresAnt, toJson(productoMapper.toResponse(guardado)),
                "Desactivó / eliminó producto '" + guardado.getNombre() + "'");
    }

    private Producto findById(Long id) {
        return productoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado con id: " + id));
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
