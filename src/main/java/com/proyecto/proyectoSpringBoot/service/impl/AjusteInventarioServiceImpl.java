package com.proyecto.proyectoSpringBoot.service.impl;

import com.proyecto.proyectoSpringBoot.dto.request.CrearAjusteRequest;
import com.proyecto.proyectoSpringBoot.dto.response.AjusteResponse;
import com.proyecto.proyectoSpringBoot.exception.ResourceNotFoundException;
import com.proyecto.proyectoSpringBoot.listener.AuditoriaHelper;
import com.proyecto.proyectoSpringBoot.mapper.AjusteMapper;
import com.proyecto.proyectoSpringBoot.model.entity.*;
import com.proyecto.proyectoSpringBoot.model.enums.TipoAjuste;
import com.proyecto.proyectoSpringBoot.model.enums.TipoOperacion;
import com.proyecto.proyectoSpringBoot.repository.*;
import com.proyecto.proyectoSpringBoot.service.interfaces.IAlertaStockService;
import com.proyecto.proyectoSpringBoot.service.interfaces.IAjusteInventarioService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class AjusteInventarioServiceImpl implements IAjusteInventarioService {

    private final AjusteInventarioRepository ajusteRepository;
    private final BodegaRepository bodegaRepository;
    private final ProductoRepository productoRepository;
    private final InventarioBodegaRepository inventarioRepository;
    private final UsuarioRepository usuarioRepository;
    private final AjusteMapper mapper;
    private final IAlertaStockService alertaStockService;
    private final AuditoriaHelper auditoriaHelper;

    @Override
    public AjusteResponse registrarAjuste(CrearAjusteRequest request, String emailUsuario) {
        Bodega bodega = bodegaRepository.findById(request.getBodegaId())
                .orElseThrow(() -> new ResourceNotFoundException("Bodega no encontrada: " + request.getBodegaId()));
        if (!bodega.isActivo()) {
            throw new IllegalStateException("No se pueden registrar ajustes en una bodega inactiva: " + bodega.getNombre());
        }

        Producto producto = productoRepository.findById(request.getProductoId())
                .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado: " + request.getProductoId()));
        if (!producto.isActivo()) {
            throw new IllegalStateException("No se pueden registrar ajustes en un producto inactivo: " + producto.getNombre());
        }

        if (request.getCantidadNueva() < 0) {
            throw new IllegalArgumentException("La cantidad nueva no puede ser negativa");
        }

        Usuario usuario = usuarioRepository.findByEmail(emailUsuario)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado: " + emailUsuario));

        InventarioBodega inventario = inventarioRepository
                .findByBodegaIdAndProductoId(bodega.getId(), producto.getId())
                .orElseGet(() -> InventarioBodega.builder()
                        .bodega(bodega)
                        .producto(producto)
                        .stockActual(0)
                        .build());

        int cantidadAnterior = inventario.getStockActual() != null ? inventario.getStockActual() : 0;
        int cantidadNueva = request.getCantidadNueva();
        int diferencia = cantidadNueva - cantidadAnterior;

        inventario.setStockActual(cantidadNueva);
        inventarioRepository.save(inventario);

        int stockGlobalNuevo = (producto.getStock() != null ? producto.getStock() : 0) + diferencia;
        if (stockGlobalNuevo < 0) stockGlobalNuevo = 0;
        producto.setStock(stockGlobalNuevo);
        productoRepository.save(producto);

        alertaStockService.verificarYGenerarAlerta(producto, bodega, cantidadNueva);

        AjusteInventario ajuste = AjusteInventario.builder()
                .bodega(bodega)
                .producto(producto)
                .tipoAjuste(request.getTipoAjuste())
                .cantidadAnterior(cantidadAnterior)
                .cantidadNueva(cantidadNueva)
                .diferencia(diferencia)
                .justificacion(request.getJustificacion())
                .usuario(usuario)
                .fecha(LocalDateTime.now())
                .build();

        AjusteInventario guardado = ajusteRepository.save(ajuste);
        AjusteResponse resp = mapper.toResponse(guardado);

        auditoriaHelper.publishAudit("AjusteInventario", guardado.getId(), TipoOperacion.INSERT, null, auditoriaHelper.toJson(resp),
                "Ajuste por " + request.getTipoAjuste() + " en " + bodega.getNombre() + " para " + producto.getNombre() +
                        " (Antes: " + cantidadAnterior + ", Nuevo: " + cantidadNueva + ", Dif: " + diferencia + ")", emailUsuario);

        return resp;
    }

    @Override
    @Transactional(readOnly = true)
    public List<AjusteResponse> listarTodos() {
        return ajusteRepository.findAllByOrderByFechaDesc().stream()
                .map(mapper::toResponse).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<AjusteResponse> listarPorBodega(Long bodegaId) {
        return ajusteRepository.findByBodegaId(bodegaId).stream()
                .map(mapper::toResponse).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<AjusteResponse> listarPorProducto(Long productoId) {
        return ajusteRepository.findByProductoId(productoId).stream()
                .map(mapper::toResponse).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<AjusteResponse> listarPorTipo(TipoAjuste tipo) {
        return ajusteRepository.findByTipoAjuste(tipo).stream()
                .map(mapper::toResponse).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public AjusteResponse obtenerPorId(Long id) {
        return mapper.toResponse(ajusteRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Ajuste no encontrado: " + id)));
    }
}
