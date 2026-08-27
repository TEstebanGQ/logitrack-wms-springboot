package com.proyecto.proyectoSpringBoot.mapper;

import com.proyecto.proyectoSpringBoot.dto.request.CrearProveedorRequest;
import com.proyecto.proyectoSpringBoot.dto.response.ProveedorResponse;
import com.proyecto.proyectoSpringBoot.model.entity.Proveedor;
import org.springframework.stereotype.Component;

@Component
public class ProveedorMapper {
    public ProveedorResponse toResponse(Proveedor p) {
        if (p == null) return null;
        ProveedorResponse r = new ProveedorResponse();
        r.setId(p.getId());
        r.setNombre(p.getNombre());
        r.setRuc(p.getRuc());
        r.setTelefono(p.getTelefono());
        r.setEmail(p.getEmail());
        r.setDireccion(p.getDireccion());
        r.setActivo(p.isActivo());
        return r;
    }
    
    public Proveedor toEntity(CrearProveedorRequest r) {
        if (r == null) return null;
        return Proveedor.builder()
                .nombre(r.getNombre())
                .ruc(r.getRuc())
                .telefono(r.getTelefono())
                .email(r.getEmail())
                .direccion(r.getDireccion())
                .activo(true)
                .build();
    }
}
