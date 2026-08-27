package com.proyecto.proyectoSpringBoot.mapper;

import com.proyecto.proyectoSpringBoot.dto.request.CrearBodegaRequest;
import com.proyecto.proyectoSpringBoot.dto.response.BodegaResponse;
import com.proyecto.proyectoSpringBoot.model.entity.Bodega;
import org.springframework.stereotype.Component;

@Component
public class BodegaMapper {

    public Bodega toEntity(CrearBodegaRequest req) {
        return Bodega.builder()
                .nombre(req.getNombre())
                .ubicacion(req.getUbicacion())
                .capacidad(req.getCapacidad())
                .encargado(req.getEncargado())
                .activo(req.getActivo() != null ? req.getActivo() : true)
                .build();
    }

    public BodegaResponse toResponse(Bodega b) {
        return BodegaResponse.builder()
                .id(b.getId())
                .nombre(b.getNombre())
                .ubicacion(b.getUbicacion())
                .capacidad(b.getCapacidad())
                .encargado(b.getEncargado())
                .activo(b.isActivo())
                .createdAt(b.getCreatedAt())
                .build();
    }

    public void updateEntity(Bodega bodega, CrearBodegaRequest req) {
        bodega.setNombre(req.getNombre());
        bodega.setUbicacion(req.getUbicacion());
        bodega.setCapacidad(req.getCapacidad());
        bodega.setEncargado(req.getEncargado());
        if (req.getActivo() != null) {
            bodega.setActivo(req.getActivo());
        }
    }
}
