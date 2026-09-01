package com.proyecto.proyectoSpringBoot.service.impl;

import com.proyecto.proyectoSpringBoot.dto.request.CrearProductoRequest;
import com.proyecto.proyectoSpringBoot.dto.response.ProductoResponse;
import com.proyecto.proyectoSpringBoot.exception.ResourceNotFoundException;
import com.proyecto.proyectoSpringBoot.listener.AuditoriaHelper;
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
    private final AuditoriaHelper auditoriaHelper;

    @Override
    public ProductoResponse crear(CrearProductoRequest request) {
        Categoria categoria = categoriaRepository.findById(request.getCategoriaId())
            .orElseThrow(() -> new ResourceNotFoundException("Categoría no encontrada"));
        Producto producto = productoMapper.toEntity(request);
        producto.setCategoria(categoria);
        Producto guardado = productoRepository.save(producto);
        ProductoResponse resp = productoMapper.toResponse(guardado);

        // Asignar el stock inicial por bodegas (si viene mapa) o a la bodega elegida
        if (request.getStockPorBodega() != null && !request.getStockPorBodega().isEmpty()) {
            int totalCalculado = 0;
            for (java.util.Map.Entry<Long, Integer> entry : request.getStockPorBodega().entrySet()) {
                Long bId = entry.getKey();
                Integer cant = entry.getValue() != null ? Math.max(0, entry.getValue()) : 0;
                Bodega b = bodegaRepository.findById(bId).orElse(null);
                if (b != null) {
                    inventarioRepository.save(InventarioBodega.builder()
                            .bodega(b)
                            .producto(guardado)
                            .stockActual(cant)
                            .build());
                    totalCalculado += cant;
                }
            }
            guardado.setStock(totalCalculado);
            guardado = productoRepository.save(guardado);
        } else if (guardado.getStock() > 0) {
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

        auditoriaHelper.publishAudit("Producto", guardado.getId(), TipoOperacion.INSERT, null, auditoriaHelper.toJson(resp),
                "Creó producto '" + guardado.getNombre() + "' ($" + guardado.getPrecio() + ", Stock Inicial: " + guardado.getStock() + " u.)");

        return mapToResponseWithBodega(guardado);
    }

    @Override
    @Transactional(readOnly = true)
    public ProductoResponse obtenerPorId(Long id) {
        return mapToResponseWithBodega(findById(id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductoResponse> listarTodos() {
        return productoRepository.findAll().stream()
                .map(this::mapToResponseWithBodega).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductoResponse> listarActivos() {
        return productoRepository.findByActivoTrue().stream()
                .map(this::mapToResponseWithBodega).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductoResponse> listarConStockBajo(int umbral) {
        return productoRepository.findByStockLessThan(umbral).stream()
                .map(this::mapToResponseWithBodega).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductoResponse> listarPorCategoria(String categoria) {
        return productoRepository.findByCategoriaNombreIgnoreCase(categoria).stream()
                .map(this::mapToResponseWithBodega).collect(Collectors.toList());
    }

    @Override
    public ProductoResponse actualizar(Long id, CrearProductoRequest request) {
        Producto producto = findById(id);
        int oldStock = producto.getStock() != null ? producto.getStock() : 0;
        int newStock = request.getStock() != null ? request.getStock() : 0;
        int delta = newStock - oldStock;

        String valoresAnt = auditoriaHelper.toJson(productoMapper.toResponse(producto));
        Categoria categoria = categoriaRepository.findById(request.getCategoriaId())
            .orElseThrow(() -> new ResourceNotFoundException("Categoría no encontrada"));
        productoMapper.updateEntity(producto, request);
        producto.setCategoria(categoria);
        Producto actualizado = productoRepository.save(producto);

        // Sincronizar la distribución de stock por bodega (si viene mapa) o la diferencia (delta)
        final Producto targetProd = actualizado;
        if (request.getStockPorBodega() != null) {
            // Eliminar registros de inventario para bodegas que ya no están asignadas al producto
            List<InventarioBodega> invsExistentes = inventarioRepository.findByProductoId(targetProd.getId());
            for (InventarioBodega invExisting : invsExistentes) {
                if (!request.getStockPorBodega().containsKey(invExisting.getBodega().getId())) {
                    inventarioRepository.delete(invExisting);
                }
            }

            int totalCalculado = 0;
            for (java.util.Map.Entry<Long, Integer> entry : request.getStockPorBodega().entrySet()) {
                Long bId = entry.getKey();
                Integer cant = entry.getValue() != null ? Math.max(0, entry.getValue()) : 0;
                Bodega b = bodegaRepository.findById(bId).orElse(null);
                if (b != null) {
                    final Bodega finalBodega = b;
                    InventarioBodega inv = inventarioRepository
                            .findByBodegaIdAndProductoId(finalBodega.getId(), targetProd.getId())
                            .orElseGet(() -> InventarioBodega.builder()
                                    .bodega(finalBodega)
                                    .producto(targetProd)
                                    .stockActual(0)
                                    .build());
                    inv.setStockActual(cant);
                    inventarioRepository.save(inv);
                    totalCalculado += cant;
                }
            }
            actualizado.setStock(totalCalculado);
            actualizado = productoRepository.save(actualizado);
        } else if (delta != 0) {
            Bodega targetBodega = null;
            if (request.getBodegaId() != null) {
                targetBodega = bodegaRepository.findById(request.getBodegaId()).orElse(null);
            }
            if (targetBodega == null) {
                targetBodega = inventarioRepository.findByProductoId(targetProd.getId()).stream()
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
                        .findByBodegaIdAndProductoId(finalBodega.getId(), targetProd.getId())
                        .orElseGet(() -> InventarioBodega.builder()
                                .bodega(finalBodega)
                                .producto(targetProd)
                                .stockActual(0)
                                .build());
                inv.setStockActual(Math.max(0, inv.getStockActual() + delta));
                inventarioRepository.save(inv);
            }
        }

        ProductoResponse resp = productoMapper.toResponse(actualizado);

        auditoriaHelper.publishAudit("Producto", actualizado.getId(), TipoOperacion.UPDATE, valoresAnt, auditoriaHelper.toJson(resp),
                "Actualizó producto '" + actualizado.getNombre() + "' ($" + actualizado.getPrecio() + ", Stock: " + actualizado.getStock() + " u., Ajuste: " + (delta >= 0 ? "+" : "") + delta + " u.)");

        return mapToResponseWithBodega(actualizado);
    }

    @Override
    public void eliminar(Long id) {
        Producto producto = findById(id);
        String valoresAnt = auditoriaHelper.toJson(productoMapper.toResponse(producto));
        producto.setActivo(false);
        Producto guardado = productoRepository.save(producto);

        auditoriaHelper.publishAudit("Producto", guardado.getId(), TipoOperacion.DELETE, valoresAnt, auditoriaHelper.toJson(productoMapper.toResponse(guardado)),
                "Desactivó / eliminó producto '" + guardado.getNombre() + "'");
    }

    private Producto findById(Long id) {
        return productoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado con id: " + id));
    }

    private ProductoResponse mapToResponseWithBodega(Producto p) {
        ProductoResponse resp = productoMapper.toResponse(p);
        List<InventarioBodega> invs = inventarioRepository.findByProductoId(p.getId());
        if (invs != null && !invs.isEmpty()) {
            String bodegaStr = invs.stream()
                    .map(inv -> inv.getBodega().getNombre() + " (" + inv.getStockActual() + " u.)")
                    .collect(Collectors.joining(", "));
            resp.setBodegaNombre(bodegaStr);
            resp.setBodegaId(invs.get(0).getBodega().getId());
        } else {
            resp.setBodegaNombre("Sin asignar");
        }
        return resp;
    }
}
