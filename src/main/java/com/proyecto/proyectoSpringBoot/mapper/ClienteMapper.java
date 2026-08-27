package com.proyecto.proyectoSpringBoot.mapper;

import com.proyecto.proyectoSpringBoot.dto.request.CrearClienteRequest;
import com.proyecto.proyectoSpringBoot.dto.response.ClienteResponse;
import com.proyecto.proyectoSpringBoot.model.entity.Cliente;
import org.springframework.stereotype.Component;

@Component
public class ClienteMapper {
    public ClienteResponse toResponse(Cliente c) {
        if (c == null) return null;
        ClienteResponse r = new ClienteResponse();
        r.setId(c.getId());
        r.setNombre(c.getNombre());
        r.setRuc(c.getRuc());
        r.setTelefono(c.getTelefono());
        r.setEmail(c.getEmail());
        r.setDireccion(c.getDireccion());
        r.setActivo(c.isActivo());
        return r;
    }
    
    public Cliente toEntity(CrearClienteRequest r) {
        if (r == null) return null;
        return Cliente.builder()
                .nombre(r.getNombre())
                .ruc(r.getRuc())
                .telefono(r.getTelefono())
                .email(r.getEmail())
                .direccion(r.getDireccion())
                .activo(true)
                .build();
    }
}
