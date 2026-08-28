package com.proyecto.proyectoSpringBoot.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.proyecto.proyectoSpringBoot.dto.request.MovimientoRequest;
import com.proyecto.proyectoSpringBoot.dto.response.MovimientoResponse;
import com.proyecto.proyectoSpringBoot.event.AuditoriaEvent;
import com.proyecto.proyectoSpringBoot.exception.ResourceNotFoundException;
import com.proyecto.proyectoSpringBoot.exception.StockInsuficienteException;
import com.proyecto.proyectoSpringBoot.mapper.MovimientoMapper;
import com.proyecto.proyectoSpringBoot.model.entity.*;
import com.proyecto.proyectoSpringBoot.model.enums.TipoMovimiento;
import com.proyecto.proyectoSpringBoot.model.enums.TipoOperacion;
import com.proyecto.proyectoSpringBoot.repository.*;
import com.proyecto.proyectoSpringBoot.service.interfaces.IAlertaStockService;
import com.proyecto.proyectoSpringBoot.service.interfaces.IMovimientoService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class MovimientoServiceImpl implements IMovimientoService {

    private final MovimientoRepository movimientoRepository;
    private final ProductoRepository productoRepository;
    private final BodegaRepository bodegaRepository;
    private final UsuarioRepository usuarioRepository;
    private final InventarioBodegaRepository inventarioRepository;
    private final MovimientoMapper movimientoMapper;
    private final ProveedorRepository proveedorRepository;
    private final ClienteRepository clienteRepository;
    private final IAlertaStockService alertaStockService;
    private final ApplicationEventPublisher eventPublisher;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public MovimientoResponse registrar(MovimientoRequest request, String emailUsuario) {
        if (request.getTipoMovimiento() == null) {
            throw new IllegalArgumentException("El tipo de movimiento es obligatorio");
        }
        if (request.getTipoMovimiento() == TipoMovimiento.ENTRADA && request.getBodegaDestinoId() == null) {
            throw new IllegalArgumentException("ENTRADA requiere especificar una bodega destino");
        }
        if (request.getTipoMovimiento() == TipoMovimiento.SALIDA && request.getBodegaOrigenId() == null) {
            throw new IllegalArgumentException("SALIDA requiere especificar una bodega origen");
        }
        if (request.getTipoMovimiento() == TipoMovimiento.TRANSFERENCIA) {
            if (request.getBodegaOrigenId() == null || request.getBodegaDestinoId() == null) {
                throw new IllegalArgumentException("TRANSFERENCIA requiere especificar bodega origen y bodega destino");
            }
            if (request.getBodegaOrigenId().equals(request.getBodegaDestinoId())) {
                throw new IllegalArgumentException("La bodega origen y la bodega destino no pueden ser la misma");
            }
        }

        Usuario usuario = usuarioRepository.findByEmail(emailUsuario)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));

        Bodega origen  = request.getBodegaOrigenId()  != null ? findBodega(request.getBodegaOrigenId())  : null;
        Bodega destino = request.getBodegaDestinoId() != null ? findBodega(request.getBodegaDestinoId()) : null;

        Proveedor proveedor = request.getProveedorId() != null ? proveedorRepository.findById(request.getProveedorId()).orElse(null) : null;
        Cliente cliente = request.getClienteId() != null ? clienteRepository.findById(request.getClienteId()).orElse(null) : null;

        Movimiento movimiento = Movimiento.builder()
                .tipoMovimiento(request.getTipoMovimiento())
                .fecha(LocalDateTime.now())
                .observaciones(request.getObservaciones())
                .usuario(usuario)
                .bodegaOrigen(origen)
                .bodegaDestino(destino)
                .proveedor(proveedor)
                .cliente(cliente)
                .detalles(new ArrayList<>())
                .build();

        StringBuilder productosDesc = new StringBuilder();

        for (MovimientoRequest.DetalleRequest d : request.getDetalles()) {
            Producto producto = productoRepository.findById(d.getProductoId())
                    .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado: " + d.getProductoId()));

            procesarStock(request.getTipoMovimiento(), producto, origen, destino, d.getCantidad());

            MovimientoDetalle detalle = MovimientoDetalle.builder()
                    .movimiento(movimiento)
                    .producto(producto)
                    .cantidad(d.getCantidad())
                    .precioUnitario(producto.getPrecio())
                    .build();
            movimiento.getDetalles().add(detalle);
            productoRepository.save(producto);

            productosDesc.append(producto.getNombre()).append(" (x").append(d.getCantidad()).append(" u.) ");
        }

        Movimiento guardado = movimientoRepository.save(movimiento);
        MovimientoResponse resp = movimientoMapper.toResponse(guardado);

        String ubicacionStr = "";
        if (request.getTipoMovimiento() == TipoMovimiento.ENTRADA) {
            ubicacionStr = "en bodega '" + (destino != null ? destino.getNombre() : "N/A") + "'";
        } else if (request.getTipoMovimiento() == TipoMovimiento.SALIDA) {
            ubicacionStr = "desde bodega '" + (origen != null ? origen.getNombre() : "N/A") + "'";
        } else {
            ubicacionStr = "de '" + (origen != null ? origen.getNombre() : "N/A") + "' a '" + (destino != null ? destino.getNombre() : "N/A") + "'";
        }

        publishAudit("Movimiento", guardado.getId(), TipoOperacion.INSERT, null, toJson(resp),
                "Registró movimiento " + request.getTipoMovimiento() + " " + ubicacionStr + " - Productos: " + productosDesc.toString().trim(), emailUsuario);

        return resp;
    }

    private void procesarStock(TipoMovimiento tipo, Producto producto,
                               Bodega origen, Bodega destino, int cantidad) {
        if (cantidad <= 0) {
            throw new IllegalArgumentException("La cantidad debe ser mayor a cero");
        }

        switch (tipo) {
            case ENTRADA -> {
                if (destino == null) throw new IllegalArgumentException("ENTRADA requiere especificar una bodega destino");
                producto.setStock(producto.getStock() + cantidad);
                ajustarInventarioBodega(destino, producto, cantidad);
            }
            case SALIDA -> {
                if (origen == null) throw new IllegalArgumentException("SALIDA requiere especificar una bodega origen");
                validarStockSuficiente(producto, origen, cantidad);
                producto.setStock(producto.getStock() - cantidad);
                InventarioBodega inv = ajustarInventarioBodega(origen, producto, -cantidad);
                alertaStockService.verificarYGenerarAlerta(producto, origen, inv.getStockActual());
            }
            case TRANSFERENCIA -> {
                if (origen == null || destino == null)
                    throw new IllegalArgumentException("TRANSFERENCIA requiere especificar bodega origen y bodega destino");
                if (origen.getId().equals(destino.getId()))
                    throw new IllegalArgumentException("La bodega origen y la bodega destino no pueden ser la misma");

                validarStockSuficiente(producto, origen, cantidad);
                InventarioBodega inv = ajustarInventarioBodega(origen, producto, -cantidad);
                ajustarInventarioBodega(destino, producto, cantidad);
                alertaStockService.verificarYGenerarAlerta(producto, origen, inv.getStockActual());
            }
        }
    }

    private void validarStockSuficiente(Producto producto, Bodega bodega, int cantidad) {
        InventarioBodega inv = inventarioRepository
                .findByBodegaIdAndProductoId(bodega.getId(), producto.getId())
                .orElseThrow(() -> new StockInsuficienteException(
                        "No hay inventario registrado del producto '" + producto.getNombre() + "' en la bodega '" + bodega.getNombre() + "'"));
        if (inv.getStockActual() < cantidad)
            throw new StockInsuficienteException(
                    "Stock insuficiente en " + bodega.getNombre() + ". Disponible: " + inv.getStockActual() + " u., solicitado: " + cantidad + " u.");
    }

    private InventarioBodega ajustarInventarioBodega(Bodega bodega, Producto producto, int delta) {
        InventarioBodega inv = inventarioRepository
                .findByBodegaIdAndProductoId(bodega.getId(), producto.getId())
                .orElseGet(() -> InventarioBodega.builder()
                        .bodega(bodega).producto(producto).stockActual(0).build());
        inv.setStockActual(inv.getStockActual() + delta);
        return inventarioRepository.save(inv);
    }

    @Override
    @Transactional(readOnly = true)
    public MovimientoResponse obtenerPorId(Long id) {
        return movimientoMapper.toResponse(movimientoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Movimiento no encontrado: " + id)));
    }

    @Override
    @Transactional(readOnly = true)
    public List<MovimientoResponse> listarTodos() {
        return movimientoRepository.findAll().stream()
                .map(movimientoMapper::toResponse).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<MovimientoResponse> listarPorTipo(TipoMovimiento tipo) {
        return movimientoRepository.findByTipoMovimiento(tipo).stream()
                .map(movimientoMapper::toResponse).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<MovimientoResponse> listarPorRangoFechas(LocalDateTime inicio, LocalDateTime fin) {
        return movimientoRepository.findByFechaBetween(inicio, fin).stream()
                .map(movimientoMapper::toResponse).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<MovimientoResponse> listarPorUsuario(Long usuarioId) {
        return movimientoRepository.findByUsuarioId(usuarioId).stream()
                .map(movimientoMapper::toResponse).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<MovimientoResponse> listarPorBodega(Long bodegaId) {
        return movimientoRepository.findByBodegaId(bodegaId).stream()
                .map(movimientoMapper::toResponse).collect(Collectors.toList());
    }

    private Bodega findBodega(Long id) {
        return bodegaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Bodega no encontrada: " + id));
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
