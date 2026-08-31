package com.proyecto.proyectoSpringBoot.service;

import com.proyecto.proyectoSpringBoot.dto.request.PedidoClienteDetalleRequest;
import com.proyecto.proyectoSpringBoot.dto.request.PedidoClienteRequest;
import com.proyecto.proyectoSpringBoot.dto.response.PedidoClienteResponse;
import com.proyecto.proyectoSpringBoot.dto.response.TareaPickingResponse;
import com.proyecto.proyectoSpringBoot.exception.ResourceNotFoundException;
import com.proyecto.proyectoSpringBoot.model.enums.EstadoPicking;
import com.proyecto.proyectoSpringBoot.service.interfaces.IPedidoClienteService;
import com.proyecto.proyectoSpringBoot.service.interfaces.ITareaPickingService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class TareaPickingServiceTest {

    @Autowired
    private ITareaPickingService tareaPickingService;

    @Autowired
    private IPedidoClienteService pedidoClienteService;

    @Test
    @DisplayName("01. Debe listar tareas y actualizar recolección de picking")
    void testPickingLifecycle() {
        PedidoClienteRequest pReq = PedidoClienteRequest.builder()
                .clienteId(1L)
                .bodegaOrigenId(1L)
                .detalles(List.of(
                        PedidoClienteDetalleRequest.builder()
                                .productoId(1L)
                                .cantidadSolicitada(5)
                                .build()
                ))
                .build();
        PedidoClienteResponse pedido = pedidoClienteService.crear(pReq, "admin@logitrack.com");

        List<TareaPickingResponse> tareas = tareaPickingService.listarPorPedido(pedido.getId());
        assertFalse(tareas.isEmpty());

        TareaPickingResponse tarea = tareas.get(0);
        assertEquals(EstadoPicking.PENDIENTE, tarea.getEstado());

        TareaPickingResponse enProceso = tareaPickingService.actualizarRecoleccion(tarea.getId(), 2, "Recogiendo laptops");
        assertEquals(EstadoPicking.EN_PROCESO, enProceso.getEstado());

        TareaPickingResponse completada = tareaPickingService.actualizarRecoleccion(tarea.getId(), 5, "Picking completo");
        assertEquals(EstadoPicking.COMPLETADA, completada.getEstado());
        assertNotNull(completada.getFechaCompletada());
    }

    @Test
    @DisplayName("02. Debe cancelar tarea de picking al eliminar")
    void testCancelarTareaPicking() {
        PedidoClienteRequest pReq = PedidoClienteRequest.builder()
                .clienteId(1L)
                .bodegaOrigenId(1L)
                .detalles(List.of(
                        PedidoClienteDetalleRequest.builder()
                                .productoId(2L)
                                .cantidadSolicitada(2)
                                .build()
                ))
                .build();
        PedidoClienteResponse pedido = pedidoClienteService.crear(pReq, "admin@logitrack.com");
        List<TareaPickingResponse> tareas = tareaPickingService.listarPorPedido(pedido.getId());
        assertFalse(tareas.isEmpty());

        Long tareaId = tareas.get(0).getId();
        tareaPickingService.eliminar(tareaId);

        TareaPickingResponse cancelada = tareaPickingService.obtenerPorId(tareaId);
        assertEquals(EstadoPicking.CANCELADA, cancelada.getEstado());
    }

    @Test
    @DisplayName("03. Debe listar tareas por estado")
    void testListarPorEstado() {
        List<TareaPickingResponse> pendientes = tareaPickingService.listarPorEstado(EstadoPicking.PENDIENTE);
        assertNotNull(pendientes);
    }

    @Test
    @DisplayName("04. Debe lanzar ResourceNotFoundException si la tarea no existe")
    void testTareaNotFound() {
        assertThrows(ResourceNotFoundException.class, () -> tareaPickingService.obtenerPorId(999999L));
    }
}
