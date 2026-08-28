package com.proyecto.proyectoSpringBoot.mapper;

import com.proyecto.proyectoSpringBoot.dto.response.ConteoCiclicoDetalleResponse;
import com.proyecto.proyectoSpringBoot.dto.response.ConteoCiclicoResponse;
import com.proyecto.proyectoSpringBoot.model.entity.ConteoCiclico;
import com.proyecto.proyectoSpringBoot.model.entity.ConteoCiclicoDetalle;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.stream.Collectors;

@Component
public class ConteoCiclicoMapper {

    public ConteoCiclicoResponse toResponse(ConteoCiclico c) {
        if (c == null) return null;
        return ConteoCiclicoResponse.builder()
                .id(c.getId())
                .codigoConteo(c.getCodigoConteo())
                .bodegaId(c.getBodega() != null ? c.getBodega().getId() : null)
                .bodegaNombre(c.getBodega() != null ? c.getBodega().getNombre() : null)
                .zonaId(c.getZona() != null ? c.getZona().getId() : null)
                .zonaNombre(c.getZona() != null ? c.getZona().getNombre() : null)
                .fechaProgramada(c.getFechaProgramada())
                .fechaEjecucion(c.getFechaEjecucion())
                .estado(c.getEstado())
                .supervisorNombre(c.getSupervisor() != null ? (c.getSupervisor().getNombre() + " " + c.getSupervisor().getApellido()) : null)
                .observaciones(c.getObservaciones())
                .detalles(c.getDetalles() != null ? c.getDetalles().stream().map(this::toDetalleResponse).collect(Collectors.toList()) : Collections.emptyList())
                .build();
    }

    public ConteoCiclicoDetalleResponse toDetalleResponse(ConteoCiclicoDetalle d) {
        if (d == null) return null;
        return ConteoCiclicoDetalleResponse.builder()
                .id(d.getId())
                .productoId(d.getProducto() != null ? d.getProducto().getId() : null)
                .productoNombre(d.getProducto() != null ? d.getProducto().getNombre() : null)
                .ubicacionId(d.getUbicacion() != null ? d.getUbicacion().getId() : null)
                .codigoUbicacion(d.getUbicacion() != null ? d.getUbicacion().getCodigoUbicacion() : null)
                .stockSistema(d.getStockSistema())
                .stockFisico(d.getStockFisico())
                .diferencia(d.getDiferencia())
                .estadoLinea(d.getEstadoLinea())
                .notas(d.getNotas())
                .build();
    }
}
