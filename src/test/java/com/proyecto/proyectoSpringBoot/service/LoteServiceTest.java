package com.proyecto.proyectoSpringBoot.service;

import com.proyecto.proyectoSpringBoot.dto.request.CrearLoteRequest;
import com.proyecto.proyectoSpringBoot.dto.response.LoteResponse;
import com.proyecto.proyectoSpringBoot.exception.ResourceNotFoundException;
import com.proyecto.proyectoSpringBoot.model.enums.EstadoLote;
import com.proyecto.proyectoSpringBoot.service.interfaces.ILoteService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class LoteServiceTest {

    @Autowired
    private ILoteService loteService;

    @Test
    @DisplayName("01. Debe crear lote con stock inicial y fechas")
    void testCrearLote() {
        CrearLoteRequest req = CrearLoteRequest.builder()
                .codigoLote("LOT-2026-A1")
                .productoId(1L)
                .bodegaId(1L)
                .stockInicial(50)
                .fechaFabricacion(LocalDate.now().minusDays(10))
                .fechaVencimiento(LocalDate.now().plusMonths(6))
                .estado(EstadoLote.DISPONIBLE)
                .build();

        LoteResponse res = loteService.crearLote(req, "admin@logitrack.com");

        assertNotNull(res.getId());
        assertEquals("LOT-2026-A1", res.getCodigoLote());
        assertEquals(50, res.getStockActual());
        assertFalse(res.isVencido());
    }

    @Test
    @DisplayName("02. Debe fallar al crear lote con código duplicado en la misma bodega")
    void testCrearLoteDuplicadoFalla() {
        CrearLoteRequest req1 = CrearLoteRequest.builder()
                .codigoLote("LOT-DUP-01")
                .productoId(1L)
                .bodegaId(1L)
                .stockInicial(30)
                .build();
        loteService.crearLote(req1, "admin@logitrack.com");

        CrearLoteRequest req2 = CrearLoteRequest.builder()
                .codigoLote("LOT-DUP-01")
                .productoId(1L)
                .bodegaId(1L)
                .stockInicial(20)
                .build();

        assertThrows(RuntimeException.class, () -> loteService.crearLote(req2, "admin@logitrack.com"));
    }

    @Test
    @DisplayName("03. Debe listar todos los lotes")
    void testListarTodos() {
        List<LoteResponse> lista = loteService.listarTodos();
        assertNotNull(lista);
    }

    @Test
    @DisplayName("04. Debe listar lotes ordenados por vencimiento (FEFO)")
    void testListarFEFO() {
        CrearLoteRequest loteTardio = CrearLoteRequest.builder()
                .codigoLote("LOT-TARDE")
                .productoId(1L)
                .bodegaId(1L)
                .stockInicial(10)
                .fechaVencimiento(LocalDate.now().plusMonths(12))
                .build();
        loteService.crearLote(loteTardio, "admin@logitrack.com");

        CrearLoteRequest lotePronto = CrearLoteRequest.builder()
                .codigoLote("LOT-PRONTO")
                .productoId(1L)
                .bodegaId(1L)
                .stockInicial(10)
                .fechaVencimiento(LocalDate.now().plusMonths(1))
                .build();
        loteService.crearLote(lotePronto, "admin@logitrack.com");

        List<LoteResponse> lista = loteService.listarPorProductoYBodegaFEFO(1L, 1L);
        assertFalse(lista.isEmpty());
        assertEquals("LOT-PRONTO", lista.get(0).getCodigoLote());
    }

    @Test
    @DisplayName("05. Debe listar lotes próximos a vencer dentro de 30 días")
    void testListarProximosAVencer() {
        CrearLoteRequest lotePorVencer = CrearLoteRequest.builder()
                .codigoLote("LOT-URGENTE")
                .productoId(1L)
                .bodegaId(1L)
                .stockInicial(5)
                .fechaVencimiento(LocalDate.now().plusDays(15))
                .build();
        loteService.crearLote(lotePorVencer, "admin@logitrack.com");

        List<LoteResponse> lista = loteService.listarProximosAVencer(30);
        assertFalse(lista.isEmpty());
        assertTrue(lista.stream().anyMatch(l -> l.getCodigoLote().equals("LOT-URGENTE")));
    }

    @Test
    @DisplayName("06. Debe cambiar estado de un lote a CUARENTENA")
    void testCambiarEstadoLote() {
        CrearLoteRequest req = CrearLoteRequest.builder()
                .codigoLote("LOT-CUAR-1")
                .productoId(2L)
                .bodegaId(1L)
                .stockInicial(15)
                .build();
        LoteResponse creado = loteService.crearLote(req, "admin@logitrack.com");

        LoteResponse actualizado = loteService.actualizarEstado(creado.getId(), EstadoLote.CUARENTENA, "admin@logitrack.com");
        assertEquals(EstadoLote.CUARENTENA, actualizado.getEstado());
    }

    @Test
    @DisplayName("07. Debe obtener lote por ID")
    void testObtenerPorId() {
        CrearLoteRequest req = CrearLoteRequest.builder()
                .codigoLote("LOT-ID-TEST")
                .productoId(3L)
                .bodegaId(1L)
                .stockInicial(20)
                .build();
        LoteResponse creado = loteService.crearLote(req, "admin@logitrack.com");

        LoteResponse obtenido = loteService.obtenerPorId(creado.getId());
        assertEquals("LOT-ID-TEST", obtenido.getCodigoLote());
    }

    @Test
    @DisplayName("08. Debe lanzar ResourceNotFoundException al buscar lote inexistente")
    void testLoteInexistenteFalla() {
        assertThrows(ResourceNotFoundException.class, () -> loteService.obtenerPorId(99999L));
    }
}
