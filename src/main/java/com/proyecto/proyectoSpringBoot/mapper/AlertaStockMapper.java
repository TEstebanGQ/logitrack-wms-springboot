package com.proyecto.proyectoSpringBoot.mapper;

import com.proyecto.proyectoSpringBoot.dto.response.AlertaStockResponse;
import com.proyecto.proyectoSpringBoot.model.entity.AlertaStock;
import org.springframework.stereotype.Component;

@Component
public class AlertaStockMapper {
    public AlertaStockResponse toResponse(AlertaStock a) {
        if (a == null) return null;
        AlertaStockResponse r = new AlertaStockResponse();
        r.setId(a.getId());
        if (a.getProducto() != null) {
            r.setProductoId(a.getProducto().getId());
            r.setProductoNombre(a.getProducto().getNombre());
        }
        if (a.getBodega() != null) {
            r.setBodegaId(a.getBodega().getId());
            r.setBodegaNombre(a.getBodega().getNombre());
        }
        r.setStockActual(a.getStockActual());
        r.setStockMinimo(a.getStockMinimo());
        r.setEstado(a.getEstado() != null ? a.getEstado().name() : null);
        r.setFechaGenerada(a.getFechaGenerada());
        r.setFechaResuelta(a.getFechaResuelta());
        if (a.getResueltaPor() != null) {
            r.setResueltaPorEmail(a.getResueltaPor().getEmail());
        }
        return r;
    }
}
