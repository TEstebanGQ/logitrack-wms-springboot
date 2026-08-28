package com.proyecto.proyectoSpringBoot.mapper;

import com.proyecto.proyectoSpringBoot.dto.response.PedidoClienteDetalleResponse;
import com.proyecto.proyectoSpringBoot.dto.response.PedidoClienteResponse;
import com.proyecto.proyectoSpringBoot.model.entity.PedidoCliente;
import com.proyecto.proyectoSpringBoot.model.entity.PedidoClienteDetalle;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.stream.Collectors;

@Component
public class PedidoClienteMapper {

    public PedidoClienteResponse toResponse(PedidoCliente p) {
        if (p == null) return null;
        return PedidoClienteResponse.builder()
                .id(p.getId())
                .codigoPedido(p.getCodigoPedido())
                .clienteId(p.getCliente() != null ? p.getCliente().getId() : null)
                .clienteNombre(p.getCliente() != null ? p.getCliente().getNombre() : null)
                .bodegaOrigenId(p.getBodegaOrigen() != null ? p.getBodegaOrigen().getId() : null)
                .bodegaOrigenNombre(p.getBodegaOrigen() != null ? p.getBodegaOrigen().getNombre() : null)
                .estado(p.getEstado())
                .totalPedido(p.getTotalPedido())
                .fechaPedido(p.getFechaPedido())
                .fechaCompromiso(p.getFechaCompromiso())
                .direccionEntrega(p.getDireccionEntrega())
                .observaciones(p.getObservaciones())
                .usuarioCreadorNombre(p.getUsuarioCreador() != null ? (p.getUsuarioCreador().getNombre() + " " + p.getUsuarioCreador().getApellido()) : null)
                .detalles(p.getDetalles() != null ? p.getDetalles().stream().map(this::toDetalleResponse).collect(Collectors.toList()) : Collections.emptyList())
                .build();
    }

    public PedidoClienteDetalleResponse toDetalleResponse(PedidoClienteDetalle d) {
        if (d == null) return null;
        return PedidoClienteDetalleResponse.builder()
                .id(d.getId())
                .productoId(d.getProducto() != null ? d.getProducto().getId() : null)
                .productoNombre(d.getProducto() != null ? d.getProducto().getNombre() : null)
                .cantidadSolicitada(d.getCantidadSolicitada())
                .cantidadDespachada(d.getCantidadDespachada())
                .precioUnitario(d.getPrecioUnitario())
                .subtotal(d.getSubtotal())
                .build();
    }
}
