package com.proyecto.proyectoSpringBoot.service;

import com.proyecto.proyectoSpringBoot.dto.request.CrearCategoriaRequest;
import com.proyecto.proyectoSpringBoot.dto.response.CategoriaResponse;
import com.proyecto.proyectoSpringBoot.exception.ResourceNotFoundException;
import com.proyecto.proyectoSpringBoot.service.interfaces.ICategoriaService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class CategoriaServiceTest {

    @Autowired
    private ICategoriaService categoriaService;

    @Test
    @DisplayName("01. Debe crear una categoría correctamente")
    void testCrearCategoria() {
        CrearCategoriaRequest req = new CrearCategoriaRequest();
        req.setNombre("Jardinería y Exteriores");
        req.setDescripcion("Herramientas para jardinería");

        CategoriaResponse res = categoriaService.crear(req);
        assertNotNull(res.getId());
        assertEquals("Jardinería y Exteriores", res.getNombre());
    }

    @Test
    @DisplayName("02. Debe fallar al crear categoría con nombre duplicado")
    void testCrearCategoriaDuplicada() {
        CrearCategoriaRequest req1 = new CrearCategoriaRequest();
        req1.setNombre("Construcción");
        categoriaService.crear(req1);

        CrearCategoriaRequest req2 = new CrearCategoriaRequest();
        req2.setNombre("CONSTRUCCIÓN");

        assertThrows(RuntimeException.class, () -> categoriaService.crear(req2));
    }

    @Test
    @DisplayName("03. Debe listar categorías activas")
    void testListarCategorias() {
        List<CategoriaResponse> lista = categoriaService.listar();
        assertFalse(lista.isEmpty());
        assertTrue(lista.stream().allMatch(CategoriaResponse::isActivo));
    }

    @Test
    @DisplayName("04. Debe actualizar una categoría existente")
    void testActualizarCategoria() {
        List<CategoriaResponse> lista = categoriaService.listar();
        Long id = lista.get(0).getId();

        CrearCategoriaRequest req = new CrearCategoriaRequest();
        req.setNombre("Electrónica y Domótica");
        req.setDescripcion("Equipos electrónicos y smart home");

        CategoriaResponse act = categoriaService.actualizar(id, req);
        assertEquals("Electrónica y Domótica", act.getNombre());
    }

    @Test
    @DisplayName("05. Debe realizar soft delete de una categoría")
    void testEliminarCategoria() {
        CrearCategoriaRequest req = new CrearCategoriaRequest();
        req.setNombre("Para Borrar");
        CategoriaResponse creada = categoriaService.crear(req);

        categoriaService.eliminar(creada.getId());

        List<CategoriaResponse> activas = categoriaService.listar();
        assertTrue(activas.stream().noneMatch(c -> c.getId().equals(creada.getId())));
    }
}
