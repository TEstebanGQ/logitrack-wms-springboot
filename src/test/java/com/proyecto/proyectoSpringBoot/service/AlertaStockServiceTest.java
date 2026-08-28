package com.proyecto.proyectoSpringBoot.service;

import com.proyecto.proyectoSpringBoot.dto.response.AlertaStockResponse;
import com.proyecto.proyectoSpringBoot.exception.ResourceNotFoundException;
import com.proyecto.proyectoSpringBoot.model.entity.Bodega;
import com.proyecto.proyectoSpringBoot.model.entity.Producto;
import com.proyecto.proyectoSpringBoot.model.enums.EstadoAlerta;
import com.proyecto.proyectoSpringBoot.repository.BodegaRepository;
import com.proyecto.proyectoSpringBoot.repository.ProductoRepository;
import com.proyecto.proyectoSpringBoot.service.interfaces.IAlertaStockService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class AlertaStockServiceTest {

    @Autowired
    private IAlertaStockService alertaStockService;

    @Autowired
    private ProductoRepository productoRepository;

    @Autowired
    private BodegaRepository bodegaRepository;

    @Test
    @DisplayName("01. Debe generar alerta cuando el stockActual es menor al stockMinimo")
    void testGenerarAlertaStockBajo() {
        Producto producto = productoRepository.findAll().get(0);
        Bodega bodega = bodegaRepository.findAll().get(0);

        alertaStockService.verificarYGenerarAlerta(producto, bodega, 2);

        List<AlertaStockResponse> pendientes = alertaStockService.listarPendientes();
        assertFalse(pendientes.isEmpty());
        assertTrue(pendientes.stream().anyMatch(a -> a.getProductoId().equals(producto.getId())));
    }

    @Test
    @DisplayName("02. No debe duplicar alerta si ya existe una PENDIENTE para el mismo producto y bodega")
    void testNoDuplicarAlertaPendiente() {
        Producto producto = productoRepository.findAll().get(0);
        Bodega bodega = bodegaRepository.findAll().get(0);

        alertaStockService.verificarYGenerarAlerta(producto, bodega, 1);
        int total1 = alertaStockService.listarPendientes().size();

        alertaStockService.verificarYGenerarAlerta(producto, bodega, 1);
        int total2 = alertaStockService.listarPendientes().size();

        assertEquals(total1, total2, "No debe duplicar alertas con el mismo producto y bodega si está pendiente");
    }

    @Test
    @DisplayName("03. No debe generar alerta si stockActual >= stockMinimo")
    void testNoGenerarAlertaConStockSuficiente() {
        Producto producto = productoRepository.findAll().get(0);
        Bodega bodega = bodegaRepository.findAll().get(0);

        int totalAntes = alertaStockService.listarPendientes().size();
        alertaStockService.verificarYGenerarAlerta(producto, bodega, producto.getStockMinimo() + 100);
        int totalDespues = alertaStockService.listarPendientes().size();

        assertEquals(totalAntes, totalDespues);
    }

    @Test
    @DisplayName("04. Debe listar todas las alertas")
    void testListarTodas() {
        List<AlertaStockResponse> todas = alertaStockService.listarTodas();
        assertNotNull(todas);
    }

    @Test
    @DisplayName("05. Debe listar alertas por producto")
    void testListarPorProducto() {
        Producto producto = productoRepository.findAll().get(0);
        Bodega bodega = bodegaRepository.findAll().get(0);

        alertaStockService.verificarYGenerarAlerta(producto, bodega, 0);
        List<AlertaStockResponse> porProducto = alertaStockService.listarPorProducto(producto.getId());

        assertNotNull(porProducto);
        assertTrue(porProducto.stream().allMatch(a -> a.getProductoId().equals(producto.getId())));
    }

    @Test
    @DisplayName("06. Debe resolver una alerta de stock y marcar estado RESUELTA")
    void testResolverAlerta() {
        Producto producto = productoRepository.findAll().get(0);
        Bodega bodega = bodegaRepository.findAll().get(0);

        alertaStockService.verificarYGenerarAlerta(producto, bodega, 1);
        List<AlertaStockResponse> pendientes = alertaStockService.listarPendientes();
        AlertaStockResponse alerta = pendientes.get(pendientes.size() - 1);

        alertaStockService.resolver(alerta.getId(), "admin@logitrack.com");

        List<AlertaStockResponse> pendientesPost = alertaStockService.listarPendientes();
        assertTrue(pendientesPost.stream().noneMatch(a -> a.getId().equals(alerta.getId())));
    }

    @Test
    @DisplayName("07. Debe lanzar excepción al resolver alerta inexistente")
    void testResolverAlertaInexistente() {
        assertThrows(ResourceNotFoundException.class, () -> alertaStockService.resolver(99999L, "admin@logitrack.com"));
    }
}
