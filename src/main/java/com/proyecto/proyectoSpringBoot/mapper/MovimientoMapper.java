package com.proyecto.proyectoSpringBoot.mapper;

import com.proyecto.proyectoSpringBoot.dto.response.MovimientoResponse;
import com.proyecto.proyectoSpringBoot.model.entity.Movimiento;
import com.proyecto.proyectoSpringBoot.model.entity.MovimientoDetalle;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class MovimientoMapper {

    public MovimientoResponse toResponse(Movimiento m) {
        List<MovimientoResponse.DetalleResponse> detalles = m.getDetalles() == null ? List.of() :
                m.getDetalles().stream().map(this::toDetalleResponse).collect(Collectors.toList());

        return MovimientoResponse.builder()
                .id(m.getId())
                .tipoMovimiento(m.getTipoMovimiento())
                .fecha(m.getFecha())
                .observaciones(m.getObservaciones())
                .usuarioNombre(m.getUsuario() != null
                        ? m.getUsuario().getNombre() + " " + m.getUsuario().getApellido() : null)
                .bodegaOrigen(m.getBodegaOrigen() != null ? m.getBodegaOrigen().getNombre() : null)
                .bodegaDestino(m.getBodegaDestino() != null ? m.getBodegaDestino().getNombre() : null)
                .proveedorNombre(m.getProveedor() != null ? m.getProveedor().getNombre() : null)
                .clienteNombre(m.getCliente() != null ? m.getCliente().getNombre() : null)
                .detalles(detalles)
                .build();
    }

    private MovimientoResponse.DetalleResponse toDetalleResponse(MovimientoDetalle d) {
        return MovimientoResponse.DetalleResponse.builder()
                .productoId(d.getProducto().getId())
                .productoNombre(d.getProducto().getNombre())
                .cantidad(d.getCantidad())
                .precioUnitario(d.getPrecioUnitario())
                .build();
    }
}
