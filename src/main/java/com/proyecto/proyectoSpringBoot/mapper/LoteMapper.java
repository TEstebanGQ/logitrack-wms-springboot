package com.proyecto.proyectoSpringBoot.mapper;

import com.proyecto.proyectoSpringBoot.dto.response.LoteResponse;
import com.proyecto.proyectoSpringBoot.model.entity.Lote;
import com.proyecto.proyectoSpringBoot.model.enums.EstadoLote;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
public class LoteMapper {

    public LoteResponse toResponse(Lote entity) {
        if (entity == null) return null;

        LocalDate hoy = LocalDate.now();
        boolean vencido = entity.getFechaVencimiento() != null && entity.getFechaVencimiento().isBefore(hoy);
        boolean proximoAVencer = entity.getFechaVencimiento() != null &&
                !vencido && entity.getFechaVencimiento().isBefore(hoy.plusDays(30));

        return LoteResponse.builder()
                .id(entity.getId())
                .codigoLote(entity.getCodigoLote())
                .productoId(entity.getProducto() != null ? entity.getProducto().getId() : null)
                .productoNombre(entity.getProducto() != null ? entity.getProducto().getNombre() : null)
                .bodegaId(entity.getBodega() != null ? entity.getBodega().getId() : null)
                .bodegaNombre(entity.getBodega() != null ? entity.getBodega().getNombre() : null)
                .stockInicial(entity.getStockInicial())
                .stockActual(entity.getStockActual())
                .fechaFabricacion(entity.getFechaFabricacion())
                .fechaVencimiento(entity.getFechaVencimiento())
                .estado(vencido ? EstadoLote.VENCIDO : entity.getEstado())
                .proximoAVencer(proximoAVencer)
                .vencido(vencido)
                .createdAt(entity.getCreatedAt())
                .build();
    }
}
