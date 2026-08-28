package com.proyecto.proyectoSpringBoot.mapper;

import com.proyecto.proyectoSpringBoot.dto.response.AjusteResponse;
import com.proyecto.proyectoSpringBoot.model.entity.AjusteInventario;
import org.springframework.stereotype.Component;

@Component
public class AjusteMapper {

    public AjusteResponse toResponse(AjusteInventario entity) {
        if (entity == null) return null;
        return AjusteResponse.builder()
                .id(entity.getId())
                .bodegaId(entity.getBodega() != null ? entity.getBodega().getId() : null)
                .bodegaNombre(entity.getBodega() != null ? entity.getBodega().getNombre() : null)
                .productoId(entity.getProducto() != null ? entity.getProducto().getId() : null)
                .productoNombre(entity.getProducto() != null ? entity.getProducto().getNombre() : null)
                .tipoAjuste(entity.getTipoAjuste())
                .cantidadAnterior(entity.getCantidadAnterior())
                .cantidadNueva(entity.getCantidadNueva())
                .diferencia(entity.getDiferencia())
                .justificacion(entity.getJustificacion())
                .usuarioNombre(entity.getUsuario() != null ? (entity.getUsuario().getNombre() + " " + entity.getUsuario().getApellido()).trim() : "Sistema")
                .fecha(entity.getFecha())
                .build();
    }
}
