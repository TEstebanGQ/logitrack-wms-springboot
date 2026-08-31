package com.proyecto.proyectoSpringBoot.service;

import com.proyecto.proyectoSpringBoot.dto.request.GuiaDespachoRequest;
import com.proyecto.proyectoSpringBoot.dto.request.PedidoClienteDetalleRequest;
import com.proyecto.proyectoSpringBoot.dto.request.PedidoClienteRequest;
import com.proyecto.proyectoSpringBoot.dto.response.GuiaDespachoResponse;
import com.proyecto.proyectoSpringBoot.dto.response.PedidoClienteResponse;
import com.proyecto.proyectoSpringBoot.exception.ResourceNotFoundException;
import com.proyecto.proyectoSpringBoot.model.enums.EstadoEnvio;
import com.proyecto.proyectoSpringBoot.service.interfaces.IGuiaDespachoService;
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
class GuiaDespachoServiceTest {

    @Autowired
    private IGuiaDespachoService guiaDespachoService;

    @Autowired
    private IPedidoClienteService pedidoClienteService;

    @Test
    @DisplayName("01. Debe generar una guía de despacho y actualizar su estado")
    void testGenerarGuiaYActualizar() {
        PedidoClienteRequest pReq = PedidoClienteRequest.builder()
                .clienteId(1L)
                .bodegaOrigenId(1L)
                .detalles(List.of(
                        PedidoClienteDetalleRequest.builder()
                                .productoId(1L)
                                .cantidadSolicitada(1)
                                .build()
                ))
                .build();
        PedidoClienteResponse pedido = pedidoClienteService.crear(pReq, "admin@logitrack.com");

        GuiaDespachoRequest req = GuiaDespachoRequest.builder()
                .numeroGuia("GUIA-TEST-" + System.currentTimeMillis())
                .pedidoId(pedido.getId())
                .transportadoraId(1L)
                .fechaEntregaEstimada(LocalDate.now().plusDays(2))
                .conductorNombre("Juan Pérez")
                .placaVehiculo("XYZ-789")
                .costoFlete(BigDecimal.valueOf(35000))
                .build();

        GuiaDespachoResponse guia = guiaDespachoService.generarGuia(req);
        assertNotNull(guia);
        assertEquals(EstadoEnvio.EN_TRANSITO, guia.getEstadoEnvio());

        GuiaDespachoResponse entregada = guiaDespachoService.actualizarEstado(guia.getId(), EstadoEnvio.ENTREGADO, "Entregado a conformidad");
        assertEquals(EstadoEnvio.ENTREGADO, entregada.getEstadoEnvio());
        assertNotNull(entregada.getFechaEntregaReal());
    }

    @Test
    @DisplayName("02. Debe rechazar creación de guía con número duplicado")
    void testNumeroGuiaDuplicado() {
        PedidoClienteRequest pReq = PedidoClienteRequest.builder()
                .clienteId(1L)
                .bodegaOrigenId(1L)
                .detalles(List.of(
                        PedidoClienteDetalleRequest.builder()
                                .productoId(1L)
                                .cantidadSolicitada(1)
                                .build()
                ))
                .build();
        PedidoClienteResponse pedido = pedidoClienteService.crear(pReq, "admin@logitrack.com");

        String numGuia = "GUIA-DUP-001";
        GuiaDespachoRequest req = GuiaDespachoRequest.builder()
                .numeroGuia(numGuia)
                .pedidoId(pedido.getId())
                .transportadoraId(1L)
                .fechaEntregaEstimada(LocalDate.now().plusDays(2))
                .build();

        guiaDespachoService.generarGuia(req);

        assertThrows(IllegalArgumentException.class, () -> guiaDespachoService.generarGuia(req));
    }

    @Test
    @DisplayName("03. Debe listar guías por transportadora y estado")
    void testListarPorTransportadoraYEstado() {
        List<GuiaDespachoResponse> porTransportadora = guiaDespachoService.listarPorTransportadora(1L);
        assertNotNull(porTransportadora);

        List<GuiaDespachoResponse> porEstado = guiaDespachoService.listarPorEstado(EstadoEnvio.EN_TRANSITO);
        assertNotNull(porEstado);
    }

    @Test
    @DisplayName("04. Debe lanzar ResourceNotFoundException si la guía no existe")
    void testGuiaNotFound() {
        assertThrows(ResourceNotFoundException.class, () -> guiaDespachoService.obtenerPorId(999999L));
    }
}
