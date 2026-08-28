package com.proyecto.proyectoSpringBoot.service;

import com.proyecto.proyectoSpringBoot.dto.request.MovimientoRequest;
import com.proyecto.proyectoSpringBoot.dto.response.MovimientoResponse;
import com.proyecto.proyectoSpringBoot.exception.StockInsuficienteException;
import com.proyecto.proyectoSpringBoot.model.enums.TipoMovimiento;
import com.proyecto.proyectoSpringBoot.service.interfaces.IMovimientoService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class MovimientoServiceTest {

    @Autowired
    private IMovimientoService movimientoService;

    @Test
    @DisplayName("17. Debe registrar movimiento de ENTRADA incrementando stock")
    void testRegistrarEntrada() {
        MovimientoRequest req = MovimientoRequest.builder()
                .tipoMovimiento(TipoMovimiento.ENTRADA)
                .bodegaDestinoId(1L)
                .observaciones("Ingreso de prueba")
                .detalles(List.of(
                        MovimientoRequest.DetalleRequest.builder()
                                .productoId(1L)
                                .cantidad(10)
                                .build()
                ))
                .build();

        MovimientoResponse res = movimientoService.registrar(req, "admin@logitrack.com");

        assertNotNull(res.getId());
        assertEquals(TipoMovimiento.ENTRADA, res.getTipoMovimiento());
        assertEquals("Bodega Central", res.getBodegaDestino());
    }

    @Test
    @DisplayName("18. Debe lanzar error al registrar ENTRADA sin bodega destino")
    void testEntradaSinDestinoFalla() {
        MovimientoRequest req = MovimientoRequest.builder()
                .tipoMovimiento(TipoMovimiento.ENTRADA)
                .detalles(List.of(MovimientoRequest.DetalleRequest.builder().productoId(1L).cantidad(5).build()))
                .build();

        assertThrows(IllegalArgumentException.class, () -> movimientoService.registrar(req, "admin@logitrack.com"));
    }

    @Test
    @DisplayName("19. Debe registrar movimiento de SALIDA cuando hay stock suficiente")
    void testRegistrarSalidaExitosa() {
        MovimientoRequest req = MovimientoRequest.builder()
                .tipoMovimiento(TipoMovimiento.SALIDA)
                .bodegaOrigenId(1L)
                .observaciones("Salida de prueba")
                .detalles(List.of(
                        MovimientoRequest.DetalleRequest.builder()
                                .productoId(1L)
                                .cantidad(5)
                                .build()
                ))
                .build();

        MovimientoResponse res = movimientoService.registrar(req, "admin@logitrack.com");

        assertNotNull(res.getId());
        assertEquals(TipoMovimiento.SALIDA, res.getTipoMovimiento());
    }

    @Test
    @DisplayName("20. Debe lanzar StockInsuficienteException en SALIDA si la cantidad supera el inventario de la bodega")
    void testSalidaStockInsuficienteFalla() {
        MovimientoRequest req = MovimientoRequest.builder()
                .tipoMovimiento(TipoMovimiento.SALIDA)
                .bodegaOrigenId(3L)
                .detalles(List.of(
                        MovimientoRequest.DetalleRequest.builder()
                                .productoId(1L)
                                .cantidad(50)
                                .build()
                ))
                .build();

        assertThrows(StockInsuficienteException.class, () -> movimientoService.registrar(req, "admin@logitrack.com"));
    }

    @Test
    @DisplayName("21. Debe registrar TRANSFERENCIA entre dos bodegas distintas")
    void testTransferenciaExitosa() {
        MovimientoRequest req = MovimientoRequest.builder()
                .tipoMovimiento(TipoMovimiento.TRANSFERENCIA)
                .bodegaOrigenId(1L)
                .bodegaDestinoId(2L)
                .observaciones("Transferencia Bogotá a Medellín")
                .detalles(List.of(
                        MovimientoRequest.DetalleRequest.builder()
                                .productoId(1L)
                                .cantidad(5)
                                .build()
                ))
                .build();

        MovimientoResponse res = movimientoService.registrar(req, "admin@logitrack.com");

        assertNotNull(res.getId());
        assertEquals(TipoMovimiento.TRANSFERENCIA, res.getTipoMovimiento());
        assertEquals("Bodega Central", res.getBodegaOrigen());
        assertEquals("Bodega Norte", res.getBodegaDestino());
    }

    @Test
    @DisplayName("22. Debe fallar la TRANSFERENCIA si la bodega origen y destino son la misma")
    void testTransferenciaMismaBodegaFalla() {
        MovimientoRequest req = MovimientoRequest.builder()
                .tipoMovimiento(TipoMovimiento.TRANSFERENCIA)
                .bodegaOrigenId(1L)
                .bodegaDestinoId(1L)
                .detalles(List.of(
                        MovimientoRequest.DetalleRequest.builder()
                                .productoId(1L)
                                .cantidad(5)
                                .build()
                ))
                .build();

        Exception ex = assertThrows(IllegalArgumentException.class, () -> movimientoService.registrar(req, "admin@logitrack.com"));
        assertTrue(ex.getMessage().contains("no pueden ser la misma"));
    }

    @Test
    @DisplayName("23. Debe fallar si la cantidad del movimiento es menor o igual a cero")
    void testCantidadInvalidaFalla() {
        MovimientoRequest req = MovimientoRequest.builder()
                .tipoMovimiento(TipoMovimiento.ENTRADA)
                .bodegaDestinoId(1L)
                .detalles(List.of(
                        MovimientoRequest.DetalleRequest.builder()
                                .productoId(1L)
                                .cantidad(0)
                                .build()
                ))
                .build();

        assertThrows(IllegalArgumentException.class, () -> movimientoService.registrar(req, "admin@logitrack.com"));
    }

    @Test
    @DisplayName("24. Debe listar todos los movimientos registrados")
    void testListarTodosLosMovimientos() {
        List<MovimientoResponse> lista = movimientoService.listarTodos();
        assertNotNull(lista);
    }

    @Test
    @DisplayName("25. Debe listar movimientos por tipo")
    void testListarPorTipo() {
        List<MovimientoResponse> entradas = movimientoService.listarPorTipo(TipoMovimiento.ENTRADA);
        assertNotNull(entradas);
    }

    @Test
    @DisplayName("26. Debe listar movimientos filtrados por bodega")
    void testListarPorBodega() {
        List<MovimientoResponse> porBodega = movimientoService.listarPorBodega(1L);
        assertNotNull(porBodega);
    }

    @Test
    @DisplayName("27. Debe registrar ENTRADA asociada a un Proveedor y asociar su nombre")
    void testRegistrarEntradaConProveedor() {
        MovimientoRequest req = MovimientoRequest.builder()
                .tipoMovimiento(TipoMovimiento.ENTRADA)
                .bodegaDestinoId(1L)
                .proveedorId(1L)
                .observaciones("Recepción proveedor")
                .detalles(List.of(
                        MovimientoRequest.DetalleRequest.builder()
                                .productoId(1L)
                                .cantidad(8)
                                .build()
                ))
                .build();

        MovimientoResponse res = movimientoService.registrar(req, "admin@logitrack.com");
        assertNotNull(res.getId());
        assertNotNull(res.getProveedorNombre());
    }

    @Test
    @DisplayName("28. Debe registrar SALIDA asociada a un Cliente y asociar su nombre")
    void testRegistrarSalidaConCliente() {
        MovimientoRequest req = MovimientoRequest.builder()
                .tipoMovimiento(TipoMovimiento.SALIDA)
                .bodegaOrigenId(1L)
                .clienteId(1L)
                .observaciones("Despacho a cliente corporativo")
                .detalles(List.of(
                        MovimientoRequest.DetalleRequest.builder()
                                .productoId(1L)
                                .cantidad(2)
                                .build()
                ))
                .build();

        MovimientoResponse res = movimientoService.registrar(req, "admin@logitrack.com");
        assertNotNull(res.getId());
        assertNotNull(res.getClienteNombre());
    }
}
