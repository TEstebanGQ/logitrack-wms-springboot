package com.proyecto.proyectoSpringBoot.mapper;

import com.proyecto.proyectoSpringBoot.dto.response.OrdenCompraResponse;
import com.proyecto.proyectoSpringBoot.model.entity.OrdenCompra;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.stream.Collectors;

@Component
public class OrdenCompraMapper {

    public OrdenCompraResponse toResponse(OrdenCompra entity) {
        if (entity == null) return null;

        return OrdenCompraResponse.builder()
                .id(entity.getId())
                .codigoOrden(entity.getCodigoOrden())
                .proveedorId(entity.getProveedor() != null ? entity.getProveedor().getId() : null)
                .proveedorNombre(entity.getProveedor() != null ? entity.getProveedor().getNombre() : null)
                .bodegaDestinoId(entity.getBodegaDestino() != null ? entity.getBodegaDestino().getId() : null)
                .bodegaDestinoNombre(entity.getBodegaDestino() != null ? entity.getBodegaDestino().getNombre() : null)
                .estado(entity.getEstado())
                .totalEstimado(entity.getTotalEstimado())
                .fechaSolicitud(entity.getFechaSolicitud())
                .fechaEntregaEsperada(entity.getFechaEntregaEsperada())
                .observaciones(entity.getObservaciones())
                .usuarioSolicitante(entity.getUsuarioSolicitante() != null ?
                        (entity.getUsuarioSolicitante().getNombre() + " " + entity.getUsuarioSolicitante().getApellido()).trim() : "Sistema")
                .detalles(entity.getDetalles() != null ?
                        entity.getDetalles().stream().map(d -> OrdenCompraResponse.DetalleOrdenResponse.builder()
                                .id(d.getId())
                                .productoId(d.getProducto() != null ? d.getProducto().getId() : null)
                                .productoNombre(d.getProducto() != null ? d.getProducto().getNombre() : null)
                                .cantidad(d.getCantidad())
                                .precioUnitario(d.getPrecioUnitario())
                                .subtotal(d.getSubtotal())
                                .build()).collect(Collectors.toList()) : Collections.emptyList())
                .build();
    }
}
