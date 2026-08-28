package com.proyecto.proyectoSpringBoot.service;

import com.proyecto.proyectoSpringBoot.dto.request.PedidoClienteDetalleRequest;
import com.proyecto.proyectoSpringBoot.dto.request.PedidoClienteRequest;
import com.proyecto.proyectoSpringBoot.dto.response.PedidoClienteResponse;
import com.proyecto.proyectoSpringBoot.model.enums.EstadoPedido;
import com.proyecto.proyectoSpringBoot.service.interfaces.IPedidoClienteService;
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
class PedidoClienteServiceTest {

    @Autowired
    private IPedidoClienteService pedidoClienteService;

    @Test
    @DisplayName("01. Debe listar pedidos de clientes")
    void testListarTodos() {
        List<PedidoClienteResponse> lista = pedidoClienteService.listarTodos();
        assertNotNull(lista);
    }

    @Test
    @DisplayName("02. Debe crear un pedido de cliente y generar tareas de picking")
    void testCrearPedido() {
        PedidoClienteRequest req = PedidoClienteRequest.builder()
                .clienteId(1L)
                .bodegaOrigenId(1L)
                .fechaCompromiso(LocalDate.now().plusDays(3))
                .direccionEntrega("Calle Falsa 123")
                .observaciones("Pedido de prueba unitaria")
                .detalles(List.of(
                        PedidoClienteDetalleRequest.builder()
                                .productoId(1L)
                                .cantidadSolicitada(2)
                                .precioUnitario(BigDecimal.valueOf(4500000))
                                .build()
                ))
                .build();

        PedidoClienteResponse res = pedidoClienteService.crear(req, "admin@logitrack.com");
        assertNotNull(res);
        assertNotNull(res.getId());
        assertEquals(EstadoPedido.PENDIENTE, res.getEstado());
        assertEquals(1, res.getDetalles().size());
    }

    @Test
    @DisplayName("03. Debe despachar un pedido y descontar stock con movimiento de salida")
    void testDespacharPedido() {
        PedidoClienteRequest req = PedidoClienteRequest.builder()
                .clienteId(1L)
                .bodegaOrigenId(1L)
                .detalles(List.of(
                        PedidoClienteDetalleRequest.builder()
                                .productoId(1L)
                                .cantidadSolicitada(1)
                                .build()
                ))
                .build();

        PedidoClienteResponse creado = pedidoClienteService.crear(req, "admin@logitrack.com");
        PedidoClienteResponse despachado = pedidoClienteService.despacharPedido(creado.getId(), "admin@logitrack.com");

        assertEquals(EstadoPedido.DESPACHADO, despachado.getEstado());
        assertEquals(1, despachado.getDetalles().get(0).getCantidadDespachada());
    }

    @Test
    @DisplayName("04. Debe cancelar un pedido no despachado")
    void testCancelarPedido() {
        PedidoClienteRequest req = PedidoClienteRequest.builder()
                .clienteId(1L)
                .bodegaOrigenId(1L)
                .detalles(List.of(
                        PedidoClienteDetalleRequest.builder()
                                .productoId(1L)
                                .cantidadSolicitada(1)
                                .build()
                ))
                .build();

        PedidoClienteResponse creado = pedidoClienteService.crear(req, "admin@logitrack.com");
        pedidoClienteService.cancelar(creado.getId(), "Cliente canceló la solicitud");

        PedidoClienteResponse cancelado = pedidoClienteService.obtenerPorId(creado.getId());
        assertEquals(EstadoPedido.CANCELADO, cancelado.getEstado());
    }
}
