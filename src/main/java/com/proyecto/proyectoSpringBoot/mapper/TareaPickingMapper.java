package com.proyecto.proyectoSpringBoot.mapper;

import com.proyecto.proyectoSpringBoot.dto.response.TareaPickingResponse;
import com.proyecto.proyectoSpringBoot.model.entity.TareaPicking;
import org.springframework.stereotype.Component;

@Component
public class TareaPickingMapper {

    public TareaPickingResponse toResponse(TareaPicking t) {
        if (t == null) return null;
        return TareaPickingResponse.builder()
                .id(t.getId())
                .codigoTarea(t.getCodigoTarea())
                .pedidoId(t.getPedido() != null ? t.getPedido().getId() : null)
                .codigoPedido(t.getPedido() != null ? t.getPedido().getCodigoPedido() : null)
                .productoId(t.getProducto() != null ? t.getProducto().getId() : null)
                .productoNombre(t.getProducto() != null ? t.getProducto().getNombre() : null)
                .ubicacionOrigenId(t.getUbicacionOrigen() != null ? t.getUbicacionOrigen().getId() : null)
                .codigoUbicacion(t.getUbicacionOrigen() != null ? t.getUbicacionOrigen().getCodigoUbicacion() : null)
                .usuarioAsignadoId(t.getUsuarioAsignado() != null ? t.getUsuarioAsignado().getId() : null)
                .usuarioAsignadoNombre(t.getUsuarioAsignado() != null ? (t.getUsuarioAsignado().getNombre() + " " + t.getUsuarioAsignado().getApellido()) : null)
                .cantidadRequerida(t.getCantidadRequerida())
                .cantidadRecogida(t.getCantidadRecogida())
                .estado(t.getEstado())
                .fechaAsignacion(t.getFechaAsignacion())
                .fechaCompletada(t.getFechaCompletada())
                .notas(t.getNotas())
                .build();
    }
}
