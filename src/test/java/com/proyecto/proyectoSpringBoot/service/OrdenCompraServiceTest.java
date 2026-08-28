package com.proyecto.proyectoSpringBoot.service;

import com.proyecto.proyectoSpringBoot.dto.request.CrearOrdenCompraRequest;
import com.proyecto.proyectoSpringBoot.dto.response.OrdenCompraResponse;
import com.proyecto.proyectoSpringBoot.exception.ResourceNotFoundException;
import com.proyecto.proyectoSpringBoot.model.enums.EstadoOrdenCompra;
import com.proyecto.proyectoSpringBoot.service.interfaces.IOrdenCompraService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class OrdenCompraServiceTest {

    @Autowired
    private IOrdenCompraService ordenCompraService;

    @Test
    @DisplayName("01. Debe crear orden de compra en estado PENDIENTE con total calculado")
    void testCrearOrdenCompra() {
        CrearOrdenCompraRequest req = CrearOrdenCompraRequest.builder()
                .proveedorId(1L)
                .bodegaDestinoId(1L)
                .fechaEntregaEsperada(LocalDate.now().plusDays(7))
                .observaciones("Pedido mensual laptops")
                .detalles(List.of(
                        CrearOrdenCompraRequest.DetalleOrdenRequest.builder()
                                .productoId(1L)
                                .cantidad(5)
                                .precioUnitario(new BigDecimal("4000000.00"))
                                .build()
                ))
                .build();

        OrdenCompraResponse res = ordenCompraService.crearOrden(req, "admin@logitrack.com");

        assertNotNull(res.getId());
        assertTrue(res.getCodigoOrden().startsWith("OC-"));
        assertEquals(EstadoOrdenCompra.PENDIENTE, res.getEstado());
        assertEquals(new BigDecimal("20000000.00"), res.getTotalEstimado());
    }

    @Test
    @DisplayName("02. Debe listar todas las órdenes de compra")
    void testListarTodas() {
        List<OrdenCompraResponse> lista = ordenCompraService.listarTodas();
        assertNotNull(lista);
    }

    @Test
    @DisplayName("03. Debe aprobar orden de compra pendiente")
    void testAprobarOrden() {
        CrearOrdenCompraRequest req = CrearOrdenCompraRequest.builder()
                .proveedorId(1L)
                .bodegaDestinoId(1L)
                .detalles(List.of(
                        CrearOrdenCompraRequest.DetalleOrdenRequest.builder()
                                .productoId(2L)
                                .cantidad(10)
                                .precioUnitario(new BigDecimal("800000.00"))
                                .build()
                ))
                .build();
        OrdenCompraResponse creada = ordenCompraService.crearOrden(req, "admin@logitrack.com");

        OrdenCompraResponse aprobada = ordenCompraService.aprobarOrden(creada.getId(), "admin@logitrack.com");
        assertEquals(EstadoOrdenCompra.APROBADA, aprobada.getEstado());
    }

    @Test
    @DisplayName("04. Debe cancelar orden de compra")
    void testCancelarOrden() {
        CrearOrdenCompraRequest req = CrearOrdenCompraRequest.builder()
                .proveedorId(1L)
                .bodegaDestinoId(1L)
                .detalles(List.of(
                        CrearOrdenCompraRequest.DetalleOrdenRequest.builder()
                                .productoId(3L)
                                .cantidad(4)
                                .precioUnitario(new BigDecimal("200000.00"))
                                .build()
                ))
                .build();
        OrdenCompraResponse creada = ordenCompraService.crearOrden(req, "admin@logitrack.com");

        OrdenCompraResponse cancelada = ordenCompraService.cancelarOrden(creada.getId(), "Proveedor sin stock", "admin@logitrack.com");
        assertEquals(EstadoOrdenCompra.CANCELADA, cancelada.getEstado());
    }

    @Test
    @DisplayName("05. Debe recibir orden de compra ingresando mercancía automáticamente a bodega")
    void testRecibirOrden() {
        CrearOrdenCompraRequest req = CrearOrdenCompraRequest.builder()
                .proveedorId(1L)
                .bodegaDestinoId(1L)
                .detalles(List.of(
                        CrearOrdenCompraRequest.DetalleOrdenRequest.builder()
                                .productoId(1L)
                                .cantidad(3)
                                .precioUnitario(new BigDecimal("4000000.00"))
                                .build()
                ))
                .build();
        OrdenCompraResponse creada = ordenCompraService.crearOrden(req, "admin@logitrack.com");

        OrdenCompraResponse recibida = ordenCompraService.recibirOrden(creada.getId(), "admin@logitrack.com");
        assertEquals(EstadoOrdenCompra.RECIBIDA, recibida.getEstado());
    }

    @Test
    @DisplayName("06. Debe fallar al recibir una orden ya recibida")
    void testRecibirOrdenDuplicadaFalla() {
        CrearOrdenCompraRequest req = CrearOrdenCompraRequest.builder()
                .proveedorId(1L)
                .bodegaDestinoId(1L)
                .detalles(List.of(
                        CrearOrdenCompraRequest.DetalleOrdenRequest.builder()
                                .productoId(1L)
                                .cantidad(1)
                                .precioUnitario(new BigDecimal("4000000.00"))
                                .build()
                ))
                .build();
        OrdenCompraResponse creada = ordenCompraService.crearOrden(req, "admin@logitrack.com");
        ordenCompraService.recibirOrden(creada.getId(), "admin@logitrack.com");

        assertThrows(IllegalStateException.class, () -> ordenCompraService.recibirOrden(creada.getId(), "admin@logitrack.com"));
    }

    @Test
    @DisplayName("07. Debe filtrar órdenes por proveedor y por estado")
    void testFiltrarPorProveedorYEstado() {
        CrearOrdenCompraRequest req = CrearOrdenCompraRequest.builder()
                .proveedorId(2L)
                .bodegaDestinoId(1L)
                .detalles(List.of(
                        CrearOrdenCompraRequest.DetalleOrdenRequest.builder()
                                .productoId(5L)
                                .cantidad(2)
                                .precioUnitario(new BigDecimal("1000000.00"))
                                .build()
                ))
                .build();
        ordenCompraService.crearOrden(req, "admin@logitrack.com");

        List<OrdenCompraResponse> porProv = ordenCompraService.listarPorProveedor(2L);
        assertFalse(porProv.isEmpty());

        List<OrdenCompraResponse> porEstado = ordenCompraService.listarPorEstado(EstadoOrdenCompra.PENDIENTE);
        assertFalse(porEstado.isEmpty());
    }

    @Test
    @DisplayName("08. Debe lanzar ResourceNotFoundException al buscar orden inexistente")
    void testBuscarInexistenteFalla() {
        assertThrows(ResourceNotFoundException.class, () -> ordenCompraService.obtenerPorId(99999L));
    }
}
