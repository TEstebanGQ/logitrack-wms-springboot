package com.proyecto.proyectoSpringBoot.service.interfaces;

import com.proyecto.proyectoSpringBoot.dto.request.CrearCategoriaRequest;
import com.proyecto.proyectoSpringBoot.dto.response.CategoriaResponse;
import java.util.List;

public interface ICategoriaService {
    CategoriaResponse crear(CrearCategoriaRequest request);
    List<CategoriaResponse> listar();
    CategoriaResponse obtenerPorId(Long id);
    CategoriaResponse actualizar(Long id, CrearCategoriaRequest request);
    void eliminar(Long id);
}
