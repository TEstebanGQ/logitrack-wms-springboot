package com.proyecto.proyectoSpringBoot.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.proyecto.proyectoSpringBoot.dto.request.CrearOrdenCompraRequest;
import com.proyecto.proyectoSpringBoot.dto.request.MovimientoRequest;
import com.proyecto.proyectoSpringBoot.dto.response.OrdenCompraResponse;
import com.proyecto.proyectoSpringBoot.event.AuditoriaEvent;
import com.proyecto.proyectoSpringBoot.exception.ResourceNotFoundException;
import com.proyecto.proyectoSpringBoot.mapper.OrdenCompraMapper;
import com.proyecto.proyectoSpringBoot.model.entity.*;
import com.proyecto.proyectoSpringBoot.model.enums.EstadoOrdenCompra;
import com.proyecto.proyectoSpringBoot.model.enums.TipoMovimiento;
import com.proyecto.proyectoSpringBoot.model.enums.TipoOperacion;
import com.proyecto.proyectoSpringBoot.repository.*;
import com.proyecto.proyectoSpringBoot.service.interfaces.IMovimientoService;
import com.proyecto.proyectoSpringBoot.service.interfaces.IOrdenCompraService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class OrdenCompraServiceImpl implements IOrdenCompraService {

    private final OrdenCompraRepository ordenCompraRepository;
    private final ProveedorRepository proveedorRepository;
    private final BodegaRepository bodegaRepository;
    private final ProductoRepository productoRepository;
    private final UsuarioRepository usuarioRepository;
    private final IMovimientoService movimientoService;
    private final OrdenCompraMapper mapper;
    private final ApplicationEventPublisher eventPublisher;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public OrdenCompraResponse crearOrden(CrearOrdenCompraRequest request, String emailUsuario) {
        Proveedor proveedor = proveedorRepository.findById(request.getProveedorId())
                .orElseThrow(() -> new ResourceNotFoundException("Proveedor no encontrado: " + request.getProveedorId()));
        if (!proveedor.isActivo()) {
            throw new IllegalStateException("No se pueden emitir órdenes a un proveedor inactivo: " + proveedor.getNombre());
        }

        Bodega bodegaDestino = bodegaRepository.findById(request.getBodegaDestinoId())
                .orElseThrow(() -> new ResourceNotFoundException("Bodega no encontrada: " + request.getBodegaDestinoId()));
        if (!bodegaDestino.isActivo()) {
            throw new IllegalStateException("No se pueden recibir órdenes en una bodega inactiva: " + bodegaDestino.getNombre());
        }

        Usuario usuario = usuarioRepository.findByEmail(emailUsuario)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado: " + emailUsuario));

        String codigoOrden = generarCodigoOrden();

        OrdenCompra orden = OrdenCompra.builder()
                .codigoOrden(codigoOrden)
                .proveedor(proveedor)
                .bodegaDestino(bodegaDestino)
                .estado(EstadoOrdenCompra.PENDIENTE)
                .fechaSolicitud(LocalDateTime.now())
                .fechaEntregaEsperada(request.getFechaEntregaEsperada())
                .observaciones(request.getObservaciones())
                .usuarioSolicitante(usuario)
                .detalles(new ArrayList<>())
                .build();

        BigDecimal total = BigDecimal.ZERO;

        for (CrearOrdenCompraRequest.DetalleOrdenRequest d : request.getDetalles()) {
            Producto producto = productoRepository.findById(d.getProductoId())
                    .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado: " + d.getProductoId()));

            BigDecimal subtotal = d.getPrecioUnitario().multiply(BigDecimal.valueOf(d.getCantidad()));
            total = total.add(subtotal);

            OrdenCompraDetalle detalle = OrdenCompraDetalle.builder()
                    .ordenCompra(orden)
                    .producto(producto)
                    .cantidad(d.getCantidad())
                    .precioUnitario(d.getPrecioUnitario())
                    .subtotal(subtotal)
                    .build();

            orden.getDetalles().add(detalle);
        }

        orden.setTotalEstimado(total);
        OrdenCompra guardada = ordenCompraRepository.save(orden);
        OrdenCompraResponse resp = mapper.toResponse(guardada);

        publishAudit("OrdenCompra", guardada.getId(), TipoOperacion.INSERT, null, toJson(resp),
                "Creó Orden de Compra " + guardada.getCodigoOrden() + " para proveedor " + proveedor.getNombre() + " por $" + total, emailUsuario);

        return resp;
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrdenCompraResponse> listarTodas() {
        return ordenCompraRepository.findAllByOrderByFechaSolicitudDesc().stream()
                .map(mapper::toResponse).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrdenCompraResponse> listarPorProveedor(Long proveedorId) {
        return ordenCompraRepository.findByProveedorId(proveedorId).stream()
                .map(mapper::toResponse).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrdenCompraResponse> listarPorEstado(EstadoOrdenCompra estado) {
        return ordenCompraRepository.findByEstado(estado).stream()
                .map(mapper::toResponse).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public OrdenCompraResponse obtenerPorId(Long id) {
        return mapper.toResponse(ordenCompraRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Orden de compra no encontrada: " + id)));
    }

    @Override
    public OrdenCompraResponse aprobarOrden(Long id, String emailUsuario) {
        OrdenCompra orden = ordenCompraRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Orden de compra no encontrada: " + id));

        if (orden.getEstado() != EstadoOrdenCompra.PENDIENTE) {
            throw new IllegalStateException("Solo se pueden aprobar órdenes en estado PENDIENTE");
        }

        String valAnt = toJson(mapper.toResponse(orden));
        orden.setEstado(EstadoOrdenCompra.APROBADA);
        OrdenCompra guardada = ordenCompraRepository.save(orden);
        OrdenCompraResponse resp = mapper.toResponse(guardada);

        publishAudit("OrdenCompra", guardada.getId(), TipoOperacion.UPDATE, valAnt, toJson(resp),
                "Aprobó Orden de Compra " + guardada.getCodigoOrden(), emailUsuario);

        return resp;
    }

    @Override
    public OrdenCompraResponse cancelarOrden(Long id, String motivo, String emailUsuario) {
        OrdenCompra orden = ordenCompraRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Orden de compra no encontrada: " + id));

        if (orden.getEstado() == EstadoOrdenCompra.RECIBIDA) {
            throw new IllegalStateException("No se puede cancelar una orden que ya ha sido recibida en inventario");
        }

        String valAnt = toJson(mapper.toResponse(orden));
        orden.setEstado(EstadoOrdenCompra.CANCELADA);
        if (motivo != null && !motivo.isBlank()) {
            orden.setObservaciones((orden.getObservaciones() != null ? orden.getObservaciones() + " | " : "") + "Cancelación: " + motivo);
        }
        OrdenCompra guardada = ordenCompraRepository.save(orden);
        OrdenCompraResponse resp = mapper.toResponse(guardada);

        publishAudit("OrdenCompra", guardada.getId(), TipoOperacion.UPDATE, valAnt, toJson(resp),
                "Canceló Orden de Compra " + guardada.getCodigoOrden() + ". Motivo: " + (motivo != null ? motivo : "Sin motivo"), emailUsuario);

        return resp;
    }

    @Override
    public OrdenCompraResponse recibirOrden(Long id, String emailUsuario) {
        OrdenCompra orden = ordenCompraRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Orden de compra no encontrada: " + id));

        if (orden.getEstado() == EstadoOrdenCompra.RECIBIDA) {
            throw new IllegalStateException("Esta orden de compra ya fue recibida anteriormente");
        }
        if (orden.getEstado() == EstadoOrdenCompra.CANCELADA) {
            throw new IllegalStateException("No se puede recibir una orden de compra cancelada");
        }

        List<MovimientoRequest.DetalleRequest> detallesMov = orden.getDetalles().stream()
                .map(d -> MovimientoRequest.DetalleRequest.builder()
                        .productoId(d.getProducto().getId())
                        .cantidad(d.getCantidad())
                        .build())
                .collect(Collectors.toList());

        MovimientoRequest movReq = MovimientoRequest.builder()
                .tipoMovimiento(TipoMovimiento.ENTRADA)
                .bodegaDestinoId(orden.getBodegaDestino().getId())
                .proveedorId(orden.getProveedor().getId())
                .observaciones("Recepción automática por Orden de Compra " + orden.getCodigoOrden())
                .detalles(detallesMov)
                .build();

        movimientoService.registrar(movReq, emailUsuario);

        String valAnt = toJson(mapper.toResponse(orden));
        orden.setEstado(EstadoOrdenCompra.RECIBIDA);
        OrdenCompra guardada = ordenCompraRepository.save(orden);
        OrdenCompraResponse resp = mapper.toResponse(guardada);

        publishAudit("OrdenCompra", guardada.getId(), TipoOperacion.UPDATE, valAnt, toJson(resp),
                "Recibió e ingresó a bodega la Orden de Compra " + guardada.getCodigoOrden(), emailUsuario);

        return resp;
    }

    private String generarCodigoOrden() {
        String fechaStr = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        int randomNum = 1000 + new Random().nextInt(9000);
        String codigo = "OC-" + fechaStr + "-" + randomNum;
        while (ordenCompraRepository.existsByCodigoOrden(codigo)) {
            randomNum = 1000 + new Random().nextInt(9000);
            codigo = "OC-" + fechaStr + "-" + randomNum;
        }
        return codigo;
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
