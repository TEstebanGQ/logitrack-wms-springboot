package com.proyecto.proyectoSpringBoot.mapper;

import com.proyecto.proyectoSpringBoot.dto.response.SolicitudTransferenciaResponse;
import com.proyecto.proyectoSpringBoot.dto.response.SolicitudTransferenciaResponse.DetalleSolicitudResponse;
import com.proyecto.proyectoSpringBoot.model.entity.SolicitudDetalleTransferencia;
import com.proyecto.proyectoSpringBoot.model.entity.SolicitudTransferencia;
import org.springframework.stereotype.Component;
import java.util.stream.Collectors;

@Component
public class SolicitudTransferenciaMapper {
    public SolicitudTransferenciaResponse toResponse(SolicitudTransferencia s) {
        if (s == null) return null;
        SolicitudTransferenciaResponse r = new SolicitudTransferenciaResponse();
        r.setId(s.getId());
        if (s.getBodegaOrigen() != null) r.setBodegaOrigen(s.getBodegaOrigen().getNombre());
        if (s.getBodegaDestino() != null) r.setBodegaDestino(s.getBodegaDestino().getNombre());
        if (s.getSolicitante() != null) r.setSolicitanteNombre(s.getSolicitante().getNombre());
        if (s.getAprobador() != null) r.setAprobadorNombre(s.getAprobador().getNombre());
        r.setEstado(s.getEstado() != null ? s.getEstado().name() : null);
        r.setObservaciones(s.getObservaciones());
        r.setMotivoRechazo(s.getMotivoRechazo());
        r.setTotalUnidades(s.getTotalUnidades());
        r.setFechaSolicitud(s.getFechaSolicitud());
        r.setFechaResolucion(s.getFechaResolucion());
        r.setFechaExpiracion(s.getFechaExpiracion());
        
        if (s.getDetalles() != null) {
            r.setDetalles(s.getDetalles().stream()
                    .map(this::toDetalleResponse)
                    .collect(Collectors.toList()));
        }
        return r;
    }
    
    public DetalleSolicitudResponse toDetalleResponse(SolicitudDetalleTransferencia d) {
        if (d == null) return null;
        DetalleSolicitudResponse dr = new DetalleSolicitudResponse();
        if (d.getProducto() != null) {
            dr.setProductoId(d.getProducto().getId());
            dr.setProductoNombre(d.getProducto().getNombre());
        }
        dr.setCantidad(d.getCantidad());
        return dr;
    }
}
