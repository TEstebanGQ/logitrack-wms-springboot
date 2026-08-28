package com.proyecto.proyectoSpringBoot.service;

import com.proyecto.proyectoSpringBoot.dto.request.CrearUbicacionRequest;
import com.proyecto.proyectoSpringBoot.dto.response.UbicacionBodegaResponse;
import com.proyecto.proyectoSpringBoot.exception.ResourceNotFoundException;
import com.proyecto.proyectoSpringBoot.service.interfaces.IUbicacionBodegaService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class UbicacionBodegaServiceTest {

    @Autowired
    private IUbicacionBodegaService ubicacionService;

    @Test
    @DisplayName("01. Debe crear ubicación física en bodega")
    void testCrearUbicacion() {
        CrearUbicacionRequest req = CrearUbicacionRequest.builder()
                .bodegaId(1L)
                .codigoUbicacion("P1-RACK-A-N1")
                .pasillo("Pasillo 1")
                .estante("Rack A")
                .nivel("Nivel 1")
                .capacidadMax(150)
                .descripcion("Ubicación frontal para alta rotación")
                .build();

        UbicacionBodegaResponse res = ubicacionService.crearUbicacion(req, "admin@logitrack.com");

        assertNotNull(res.getId());
        assertEquals("P1-RACK-A-N1", res.getCodigoUbicacion());
        assertTrue(res.isActivo());
    }

    @Test
    @DisplayName("02. Debe fallar al crear ubicación con código duplicado en la misma bodega")
    void testCrearUbicacionDuplicadaFalla() {
        CrearUbicacionRequest req1 = CrearUbicacionRequest.builder()
                .bodegaId(1L)
                .codigoUbicacion("POS-DUP-01")
                .pasillo("P1")
                .estante("E1")
                .nivel("N1")
                .build();
        ubicacionService.crearUbicacion(req1, "admin@logitrack.com");

        CrearUbicacionRequest req2 = CrearUbicacionRequest.builder()
                .bodegaId(1L)
                .codigoUbicacion("POS-DUP-01")
                .pasillo("P1")
                .estante("E1")
                .nivel("N1")
                .build();

        assertThrows(RuntimeException.class, () -> ubicacionService.crearUbicacion(req2, "admin@logitrack.com"));
    }

    @Test
    @DisplayName("03. Debe listar todas las ubicaciones")
    void testListarTodas() {
        List<UbicacionBodegaResponse> lista = ubicacionService.listarTodas();
        assertNotNull(lista);
    }

    @Test
    @DisplayName("04. Debe filtrar ubicaciones por bodega")
    void testListarPorBodega() {
        CrearUbicacionRequest req = CrearUbicacionRequest.builder()
                .bodegaId(2L)
                .codigoUbicacion("P2-RACK-B-N1")
                .pasillo("Pasillo 2")
                .estante("Rack B")
                .nivel("Nivel 1")
                .build();
        ubicacionService.crearUbicacion(req, "admin@logitrack.com");

        List<UbicacionBodegaResponse> lista = ubicacionService.listarPorBodega(2L, false);
        assertFalse(lista.isEmpty());
        assertTrue(lista.stream().anyMatch(u -> u.getCodigoUbicacion().equals("P2-RACK-B-N1")));
    }

    @Test
    @DisplayName("05. Debe desactivar una ubicación física")
    void testDesactivarUbicacion() {
        CrearUbicacionRequest req = CrearUbicacionRequest.builder()
                .bodegaId(1L)
                .codigoUbicacion("PARA-DESACTIVAR")
                .pasillo("P3")
                .estante("R3")
                .nivel("N3")
                .build();
        UbicacionBodegaResponse creada = ubicacionService.crearUbicacion(req, "admin@logitrack.com");

        ubicacionService.desactivarUbicacion(creada.getId(), "admin@logitrack.com");

        UbicacionBodegaResponse obtenida = ubicacionService.obtenerPorId(creada.getId());
        assertFalse(obtenida.isActivo());
    }

    @Test
    @DisplayName("06. Debe lanzar ResourceNotFoundException al buscar ubicación inexistente")
    void testUbicacionInexistenteFalla() {
        assertThrows(ResourceNotFoundException.class, () -> ubicacionService.obtenerPorId(99999L));
    }
}
