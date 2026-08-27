package com.proyecto.proyectoSpringBoot.mapper;

import com.proyecto.proyectoSpringBoot.dto.request.CrearCategoriaRequest;
import com.proyecto.proyectoSpringBoot.dto.response.CategoriaResponse;
import com.proyecto.proyectoSpringBoot.model.entity.Categoria;
import org.springframework.stereotype.Component;

@Component
public class CategoriaMapper {
    public CategoriaResponse toResponse(Categoria c) {
        if (c == null) return null;
        CategoriaResponse r = new CategoriaResponse();
        r.setId(c.getId());
        r.setNombre(c.getNombre());
        r.setDescripcion(c.getDescripcion());
        r.setActivo(c.isActivo());
        return r;
    }
    
    public Categoria toEntity(CrearCategoriaRequest r) {
        if (r == null) return null;
        return Categoria.builder()
                .nombre(r.getNombre())
                .descripcion(r.getDescripcion())
                .activo(true)
                .build();
    }
}
