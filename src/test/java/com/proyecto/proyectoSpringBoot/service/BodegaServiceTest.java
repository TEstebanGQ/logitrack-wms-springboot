package com.proyecto.proyectoSpringBoot.service;

import com.proyecto.proyectoSpringBoot.dto.request.CrearBodegaRequest;
import com.proyecto.proyectoSpringBoot.dto.response.BodegaResponse;
import com.proyecto.proyectoSpringBoot.dto.response.InventarioBodegaResponse;
import com.proyecto.proyectoSpringBoot.exception.ResourceNotFoundException;
import com.proyecto.proyectoSpringBoot.service.interfaces.IBodegaService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class BodegaServiceTest {

    @Autowired
    private IBodegaService bodegaService;

    @Test
    @DisplayName("01. Debe crear una nueva bodega correctamente")
    void testCrearBodega() {
        CrearBodegaRequest request = CrearBodegaRequest.builder()
                .nombre("Bodega Test")
                .ubicacion("Manizales, Caldas")
                .capacidad(2500)
                .encargado("Pedro Gómez")
                .build();

        BodegaResponse response = bodegaService.crear(request);

        assertNotNull(response.getId());
        assertEquals("Bodega Test", response.getNombre());
        assertEquals("Manizales, Caldas", response.getUbicacion());
        assertTrue(response.isActivo());
    }

    @Test
    @DisplayName("02. Debe obtener una bodega por ID")
    void testObtenerBodegaPorId() {
        BodegaResponse bodega = bodegaService.obtenerPorId(1L);
        assertNotNull(bodega);
        assertEquals(1L, bodega.getId());
    }

    @Test
    @DisplayName("03. Debe lanzar excepción al buscar bodega inexistente")
    void testObtenerBodegaInexistente() {
        assertThrows(ResourceNotFoundException.class, () -> bodegaService.obtenerPorId(999L));
    }

    @Test
    @DisplayName("04. Debe listar todas las bodegas")
    void testListarTodasLasBodegas() {
        List<BodegaResponse> lista = bodegaService.listarTodas();
        assertFalse(lista.isEmpty());
        assertTrue(lista.size() >= 5);
    }

    @Test
    @DisplayName("05. Debe listar solo bodegas activas")
    void testListarBodegasActivas() {
        List<BodegaResponse> activas = bodegaService.listarActivas();
        assertFalse(activas.isEmpty());
        assertTrue(activas.stream().allMatch(BodegaResponse::isActivo));
    }

    @Test
    @DisplayName("06. Debe actualizar datos de una bodega existente")
    void testActualizarBodega() {
        CrearBodegaRequest updateRequest = CrearBodegaRequest.builder()
                .nombre("Bodega Central Modificada")
                .ubicacion("Bogotá D.C.")
                .capacidad(6000)
                .encargado("Carlos G.")
                .build();

        BodegaResponse actualizada = bodegaService.actualizar(1L, updateRequest);

        assertEquals("Bodega Central Modificada", actualizada.getNombre());
        assertEquals(6000, actualizada.getCapacidad());
    }

    @Test
    @DisplayName("07. Debe realizar el borrado lógico (soft delete) de una bodega")
    void testEliminarBodega() {
        bodegaService.eliminar(5L);
        BodegaResponse eliminada = bodegaService.obtenerPorId(5L);
        assertFalse(eliminada.isActivo());
    }

    @Test
    @DisplayName("08. Debe obtener el inventario específico de una bodega")
    void testObtenerInventarioPorBodega() {
        List<InventarioBodegaResponse> inv = bodegaService.obtenerInventarioPorBodega(1L);
        assertNotNull(inv);
        assertFalse(inv.isEmpty());
        assertEquals(1L, inv.get(0).getBodegaId());
    }
}
