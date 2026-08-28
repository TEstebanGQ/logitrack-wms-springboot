package com.proyecto.proyectoSpringBoot.service;

import com.proyecto.proyectoSpringBoot.dto.request.CrearAjusteRequest;
import com.proyecto.proyectoSpringBoot.dto.response.AjusteResponse;
import com.proyecto.proyectoSpringBoot.exception.ResourceNotFoundException;
import com.proyecto.proyectoSpringBoot.model.enums.TipoAjuste;
import com.proyecto.proyectoSpringBoot.service.interfaces.IAjusteInventarioService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class AjusteInventarioServiceTest {

    @Autowired
    private IAjusteInventarioService ajusteService;

    @Test
    @DisplayName("01. Debe registrar ajuste por MERMA reduciendo el stock")
    void testRegistrarAjusteMerma() {
        CrearAjusteRequest req = CrearAjusteRequest.builder()
                .bodegaId(1L)
                .productoId(1L)
                .tipoAjuste(TipoAjuste.MERMA)
                .cantidadNueva(15) // Anterior era 20
                .justificacion("Unidades rotas durante manipulación")
                .build();

        AjusteResponse res = ajusteService.registrarAjuste(req, "admin@logitrack.com");

        assertNotNull(res.getId());
        assertEquals(TipoAjuste.MERMA, res.getTipoAjuste());
        assertEquals(20, res.getCantidadAnterior());
        assertEquals(15, res.getCantidadNueva());
        assertEquals(-5, res.getDiferencia());
    }

    @Test
    @DisplayName("02. Debe registrar ajuste por CONTEO_FISICO aumentando el stock")
    void testRegistrarAjusteConteoFisico() {
        CrearAjusteRequest req = CrearAjusteRequest.builder()
                .bodegaId(1L)
                .productoId(1L)
                .tipoAjuste(TipoAjuste.CONTEO_FISICO)
                .cantidadNueva(25) // Anterior era 20
                .justificacion("Sobrante encontrado en inventario físico")
                .build();

        AjusteResponse res = ajusteService.registrarAjuste(req, "admin@logitrack.com");

        assertNotNull(res.getId());
        assertEquals(5, res.getDiferencia());
    }

    @Test
    @DisplayName("03. Debe listar todos los ajustes registrados")
    void testListarTodos() {
        CrearAjusteRequest req = CrearAjusteRequest.builder()
                .bodegaId(1L)
                .productoId(1L)
                .tipoAjuste(TipoAjuste.DANO)
                .cantidadNueva(18)
                .justificacion("Producto averiado")
                .build();
        ajusteService.registrarAjuste(req, "admin@logitrack.com");

        List<AjusteResponse> lista = ajusteService.listarTodos();
        assertFalse(lista.isEmpty());
    }

    @Test
    @DisplayName("04. Debe filtrar ajustes por bodega")
    void testListarPorBodega() {
        CrearAjusteRequest req = CrearAjusteRequest.builder()
                .bodegaId(1L)
                .productoId(1L)
                .tipoAjuste(TipoAjuste.OTRO)
                .cantidadNueva(19)
                .justificacion("Ajuste manual")
                .build();
        ajusteService.registrarAjuste(req, "admin@logitrack.com");

        List<AjusteResponse> lista = ajusteService.listarPorBodega(1L);
        assertFalse(lista.isEmpty());
        assertTrue(lista.stream().allMatch(a -> a.getBodegaId().equals(1L)));
    }

    @Test
    @DisplayName("05. Debe filtrar ajustes por producto")
    void testListarPorProducto() {
        CrearAjusteRequest req = CrearAjusteRequest.builder()
                .bodegaId(1L)
                .productoId(2L)
                .tipoAjuste(TipoAjuste.MERMA)
                .cantidadNueva(10)
                .justificacion("Ajuste producto 2")
                .build();
        ajusteService.registrarAjuste(req, "admin@logitrack.com");

        List<AjusteResponse> lista = ajusteService.listarPorProducto(2L);
        assertFalse(lista.isEmpty());
        assertTrue(lista.stream().allMatch(a -> a.getProductoId().equals(2L)));
    }

    @Test
    @DisplayName("06. Debe filtrar ajustes por tipo de ajuste")
    void testListarPorTipo() {
        CrearAjusteRequest req = CrearAjusteRequest.builder()
                .bodegaId(1L)
                .productoId(3L)
                .tipoAjuste(TipoAjuste.VENCIMIENTO)
                .cantidadNueva(30)
                .justificacion("Lote vencido")
                .build();
        ajusteService.registrarAjuste(req, "admin@logitrack.com");

        List<AjusteResponse> lista = ajusteService.listarPorTipo(TipoAjuste.VENCIMIENTO);
        assertFalse(lista.isEmpty());
        assertTrue(lista.stream().allMatch(a -> a.getTipoAjuste() == TipoAjuste.VENCIMIENTO));
    }

    @Test
    @DisplayName("07. Debe obtener ajuste por ID")
    void testObtenerPorId() {
        CrearAjusteRequest req = CrearAjusteRequest.builder()
                .bodegaId(1L)
                .productoId(1L)
                .tipoAjuste(TipoAjuste.MERMA)
                .cantidadNueva(12)
                .justificacion("Merma test")
                .build();
        AjusteResponse guardado = ajusteService.registrarAjuste(req, "admin@logitrack.com");

        AjusteResponse obtenido = ajusteService.obtenerPorId(guardado.getId());
        assertEquals(guardado.getId(), obtenido.getId());
        assertEquals("Merma test", obtenido.getJustificacion());
    }

    @Test
    @DisplayName("08. Debe lanzar ResourceNotFoundException si bodega o producto no existen")
    void testBodegaInexistenteFalla() {
        CrearAjusteRequest req = CrearAjusteRequest.builder()
                .bodegaId(9999L)
                .productoId(1L)
                .tipoAjuste(TipoAjuste.MERMA)
                .cantidadNueva(5)
                .justificacion("Falla esperada")
                .build();

        assertThrows(ResourceNotFoundException.class, () -> ajusteService.registrarAjuste(req, "admin@logitrack.com"));
    }
}
