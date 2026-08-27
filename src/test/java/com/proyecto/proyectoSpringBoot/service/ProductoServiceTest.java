package com.proyecto.proyectoSpringBoot.service;

import com.proyecto.proyectoSpringBoot.dto.request.CrearProductoRequest;
import com.proyecto.proyectoSpringBoot.dto.response.ProductoResponse;
import com.proyecto.proyectoSpringBoot.exception.ResourceNotFoundException;
import com.proyecto.proyectoSpringBoot.service.interfaces.IProductoService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class ProductoServiceTest {

    @Autowired
    private IProductoService productoService;

    @Test
    @DisplayName("09. Debe crear un producto correctamente")
    void testCrearProducto() {
        CrearProductoRequest request = CrearProductoRequest.builder()
                .nombre("Tablet Lenovo P11")
                .categoriaId(1L)
                .stock(20)
                .precio(new BigDecimal("1200000.00"))
                .descripcion("Tablet 11 pulgadas")
                .build();

        ProductoResponse res = productoService.crear(request);

        assertNotNull(res.getId());
        assertEquals("Tablet Lenovo P11", res.getNombre());
        assertEquals(20, res.getStock());
    }

    @Test
    @DisplayName("10. Debe obtener un producto por su ID")
    void testObtenerProductoPorId() {
        ProductoResponse p = productoService.obtenerPorId(1L);
        assertNotNull(p);
        assertEquals(1L, p.getId());
        assertEquals("Laptop Dell XPS 15", p.getNombre());
    }

    @Test
    @DisplayName("11. Debe lanzar excepción al buscar producto inexistente")
    void testObtenerProductoInexistente() {
        assertThrows(ResourceNotFoundException.class, () -> productoService.obtenerPorId(9999L));
    }

    @Test
    @DisplayName("12. Debe listar todos los productos activos")
    void testListarProductosActivos() {
        List<ProductoResponse> lista = productoService.listarActivos();
        assertFalse(lista.isEmpty());
        assertTrue(lista.size() >= 10);
    }

    @Test
    @DisplayName("13. Debe listar productos con stock bajo (< 10 unidades)")
    void testListarStockBajo() {
        List<ProductoResponse> bajoStock = productoService.listarConStockBajo(10);
        assertNotNull(bajoStock);
        assertFalse(bajoStock.isEmpty());
        assertTrue(bajoStock.stream().allMatch(p -> p.getStock() < 10));
    }

    @Test
    @DisplayName("14. Debe listar productos filtrados por categoría")
    void testListarPorCategoria() {
        List<ProductoResponse> elec = productoService.listarPorCategoria("Electrónica");
        assertNotNull(elec);
        assertFalse(elec.isEmpty());
    }

    @Test
    @DisplayName("15. Debe actualizar los datos de un producto")
    void testActualizarProducto() {
        CrearProductoRequest req = CrearProductoRequest.builder()
                .nombre("Laptop Dell XPS 15 Pro")
                .categoriaId(1L)
                .stock(55)
                .precio(new BigDecimal("4800000.00"))
                .descripcion("Procesador i9")
                .build();

        ProductoResponse act = productoService.actualizar(1L, req);
        assertEquals("Laptop Dell XPS 15 Pro", act.getNombre());
        assertEquals(new BigDecimal("4800000.00"), act.getPrecio());
    }

    @Test
    @DisplayName("16. Debe realizar soft delete de un producto")
    void testEliminarProducto() {
        productoService.eliminar(2L);
        List<ProductoResponse> activos = productoService.listarActivos();
        assertTrue(activos.stream().noneMatch(p -> p.getId().equals(2L)));
    }
}
