package com.proyecto.proyectoSpringBoot.service.impl;

import com.proyecto.proyectoSpringBoot.dto.request.PedidoClienteDetalleRequest;
import com.proyecto.proyectoSpringBoot.dto.request.PedidoClienteRequest;
import com.proyecto.proyectoSpringBoot.dto.response.PedidoClienteResponse;
import com.proyecto.proyectoSpringBoot.exception.ResourceNotFoundException;
import com.proyecto.proyectoSpringBoot.mapper.PedidoClienteMapper;
import com.proyecto.proyectoSpringBoot.model.entity.*;
import com.proyecto.proyectoSpringBoot.model.enums.EstadoPedido;
import com.proyecto.proyectoSpringBoot.model.enums.EstadoPicking;
import com.proyecto.proyectoSpringBoot.model.enums.TipoMovimiento;
import com.proyecto.proyectoSpringBoot.repository.*;
import com.proyecto.proyectoSpringBoot.service.interfaces.IPedidoClienteService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class PedidoClienteServiceImpl implements IPedidoClienteService {

    private final PedidoClienteRepository pedidoClienteRepository;
    private final ClienteRepository clienteRepository;
    private final BodegaRepository bodegaRepository;
    private final ProductoRepository productoRepository;
    private final UsuarioRepository usuarioRepository;
    private final InventarioBodegaRepository inventarioBodegaRepository;
    private final MovimientoRepository movimientoRepository;
    private final TareaPickingRepository tareaPickingRepository;
    private final PedidoClienteMapper mapper;

    @Override
    @Transactional(readOnly = true)
    public List<PedidoClienteResponse> listarTodos() {
        return pedidoClienteRepository.findAll().stream()
                .map(mapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<PedidoClienteResponse> listarPorCliente(Long clienteId) {
        return pedidoClienteRepository.findByClienteId(clienteId).stream()
                .map(mapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<PedidoClienteResponse> listarPorEstado(EstadoPedido estado) {
        return pedidoClienteRepository.findByEstado(estado).stream()
                .map(mapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public PedidoClienteResponse obtenerPorId(Long id) {
        PedidoCliente p = pedidoClienteRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Pedido de cliente no encontrado con id: " + id));
        return mapper.toResponse(p);
    }

    @Override
    public PedidoClienteResponse crear(PedidoClienteRequest request, String usuarioEmail) {
        Cliente cliente = clienteRepository.findById(request.getClienteId())
                .orElseThrow(() -> new ResourceNotFoundException("Cliente no encontrado con id: " + request.getClienteId()));

        Bodega bodega = bodegaRepository.findById(request.getBodegaOrigenId())
                .orElseThrow(() -> new ResourceNotFoundException("Bodega no encontrada con id: " + request.getBodegaOrigenId()));

        Usuario usuario = usuarioRepository.findByEmail(usuarioEmail)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado con email: " + usuarioEmail));

        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        String codigoPedido = "PED-" + timestamp;

        PedidoCliente pedido = PedidoCliente.builder()
                .codigoPedido(codigoPedido)
                .cliente(cliente)
                .bodegaOrigen(bodega)
                .usuarioCreador(usuario)
                .fechaCompromiso(request.getFechaCompromiso())
                .direccionEntrega(request.getDireccionEntrega() != null ? request.getDireccionEntrega() : cliente.getDireccion())
                .observaciones(request.getObservaciones())
                .estado(EstadoPedido.PENDIENTE)
                .detalles(new ArrayList<>())
                .build();

        BigDecimal total = BigDecimal.ZERO;

        for (PedidoClienteDetalleRequest detReq : request.getDetalles()) {
            Producto producto = productoRepository.findById(detReq.getProductoId())
                    .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado con id: " + detReq.getProductoId()));

            // Validar existencia y disponibilidad de stock en la bodega de origen
            inventarioBodegaRepository.findByBodegaIdAndProductoId(bodega.getId(), producto.getId())
                    .ifPresent(inv -> {
                        if (inv.getStockActual() < detReq.getCantidadSolicitada()) {
                            throw new IllegalStateException("Stock insuficiente en " + bodega.getNombre() + " para el producto: " + producto.getNombre() + ". Disponible: " + inv.getStockActual() + " u.");
                        }
                    });

            BigDecimal precio = detReq.getPrecioUnitario() != null ? detReq.getPrecioUnitario() : producto.getPrecio();
            BigDecimal subtotal = precio.multiply(BigDecimal.valueOf(detReq.getCantidadSolicitada()));
            total = total.add(subtotal);

            PedidoClienteDetalle detalle = PedidoClienteDetalle.builder()
                    .pedido(pedido)
                    .producto(producto)
                    .cantidadSolicitada(detReq.getCantidadSolicitada())
                    .cantidadDespachada(0)
                    .precioUnitario(precio)
                    .subtotal(subtotal)
                    .build();

            pedido.getDetalles().add(detalle);
        }

        pedido.setTotalPedido(total);
        PedidoCliente guardado = pedidoClienteRepository.save(pedido);

        // Crear automáticamente tareas de picking para los operarios de la bodega
        int seq = 1;
        for (PedidoClienteDetalle d : guardado.getDetalles()) {
            TareaPicking task = TareaPicking.builder()
                    .codigoTarea("PCK-" + guardado.getId() + "-" + seq++)
                    .pedido(guardado)
                    .producto(d.getProducto())
                    .usuarioAsignado(usuario)
                    .cantidadRequerida(d.getCantidadSolicitada())
                    .cantidadRecogida(0)
                    .estado(EstadoPicking.PENDIENTE)
                    .fechaAsignacion(LocalDateTime.now())
                    .notas("Picking para pedido " + guardado.getCodigoPedido())
                    .build();
            tareaPickingRepository.save(task);
        }

        return mapper.toResponse(guardado);
    }

    @Override
    public PedidoClienteResponse cambiarEstado(Long id, EstadoPedido nuevoEstado, String observaciones) {
        PedidoCliente pedido = pedidoClienteRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Pedido de cliente no encontrado con id: " + id));

        if (nuevoEstado == EstadoPedido.EN_PREPARACION || nuevoEstado == EstadoPedido.EMPACADO) {
            // Validar que exista suficiente stock disponible en la bodega de origen para reservar
            for (PedidoClienteDetalle det : pedido.getDetalles()) {
                InventarioBodega inv = inventarioBodegaRepository.findByBodegaIdAndProductoId(pedido.getBodegaOrigen().getId(), det.getProducto().getId())
                        .orElseThrow(() -> new IllegalStateException("El producto " + det.getProducto().getNombre() + " no posee registro de inventario en la bodega " + pedido.getBodegaOrigen().getNombre()));

                if (inv.getStockActual() < det.getCantidadSolicitada()) {
                    throw new IllegalStateException("Imposible pasar a " + nuevoEstado + ": Stock insuficiente en " + pedido.getBodegaOrigen().getNombre() + " para " + det.getProducto().getNombre() + ". Disponible: " + inv.getStockActual() + " u., Requerido: " + det.getCantidadSolicitada() + " u.");
                }
            }
        }

        pedido.setEstado(nuevoEstado);
        if (observaciones != null && !observaciones.isBlank()) {
            pedido.setObservaciones(observaciones);
        }

        return mapper.toResponse(pedidoClienteRepository.save(pedido));
    }

    @Override
    public PedidoClienteResponse despacharPedido(Long id, String usuarioEmail) {
        PedidoCliente pedido = pedidoClienteRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Pedido de cliente no encontrado con id: " + id));

        if (pedido.getEstado() == EstadoPedido.DESPACHADO || pedido.getEstado() == EstadoPedido.ENTREGADO) {
            throw new IllegalStateException("El pedido ya ha sido despachado.");
        }
        if (pedido.getEstado() == EstadoPedido.CANCELADO) {
            throw new IllegalStateException("No se puede despachar un pedido cancelado.");
        }

        Usuario usuario = usuarioRepository.findByEmail(usuarioEmail)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado con email: " + usuarioEmail));

        // Registrar movimiento de SALIDA automático
        Movimiento movimiento = Movimiento.builder()
                .tipoMovimiento(TipoMovimiento.SALIDA)
                .fecha(LocalDateTime.now())
                .observaciones("Despacho oficial de pedido " + pedido.getCodigoPedido())
                .usuario(usuario)
                .bodegaOrigen(pedido.getBodegaOrigen())
                .cliente(pedido.getCliente())
                .detalles(new ArrayList<>())
                .build();

        for (PedidoClienteDetalle det : pedido.getDetalles()) {
            det.setCantidadDespachada(det.getCantidadSolicitada());

            MovimientoDetalle movDet = MovimientoDetalle.builder()
                    .movimiento(movimiento)
                    .producto(det.getProducto())
                    .cantidad(det.getCantidadSolicitada())
                    .precioUnitario(det.getPrecioUnitario())
                    .build();

            movimiento.getDetalles().add(movDet);

            // Descontar inventario en bodega
            inventarioBodegaRepository.findByBodegaIdAndProductoId(pedido.getBodegaOrigen().getId(), det.getProducto().getId())
                    .ifPresent(inv -> {
                        inv.setStockActual(Math.max(0, inv.getStockActual() - det.getCantidadSolicitada()));
                        inventarioBodegaRepository.save(inv);
                    });

            // Descontar stock global del producto
            Producto prod = det.getProducto();
            prod.setStock(Math.max(0, prod.getStock() - det.getCantidadSolicitada()));
            productoRepository.save(prod);
        }

        movimientoRepository.save(movimiento);

        // Completar picking
        List<TareaPicking> tareas = tareaPickingRepository.findByPedidoId(pedido.getId());
        for (TareaPicking t : tareas) {
            t.setEstado(EstadoPicking.COMPLETADA);
            t.setCantidadRecogida(t.getCantidadRequerida());
            t.setFechaCompletada(LocalDateTime.now());
            tareaPickingRepository.save(t);
        }

        pedido.setEstado(EstadoPedido.DESPACHADO);
        return mapper.toResponse(pedidoClienteRepository.save(pedido));
    }

    @Override
    public void cancelar(Long id, String motivo) {
        PedidoCliente pedido = pedidoClienteRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Pedido de cliente no encontrado con id: " + id));

        if (pedido.getEstado() == EstadoPedido.DESPACHADO || pedido.getEstado() == EstadoPedido.ENTREGADO) {
            throw new IllegalStateException("No se puede cancelar un pedido ya despachado.");
        }

        pedido.setEstado(EstadoPedido.CANCELADO);
        if (motivo != null) {
            pedido.setObservaciones("CANCELADO: " + motivo);
        }
        pedidoClienteRepository.save(pedido);

        // Cancelar tareas de picking asociadas
        List<TareaPicking> tareas = tareaPickingRepository.findByPedidoId(pedido.getId());
        for (TareaPicking t : tareas) {
            t.setEstado(EstadoPicking.CANCELADA);
            tareaPickingRepository.save(t);
        }
    }
}
