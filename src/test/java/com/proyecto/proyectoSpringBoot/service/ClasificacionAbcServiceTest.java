package com.proyecto.proyectoSpringBoot.service;

import com.proyecto.proyectoSpringBoot.dto.response.ClasificacionAbcResponse;
import com.proyecto.proyectoSpringBoot.service.interfaces.IReporteService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class ClasificacionAbcServiceTest {

    @Autowired
    private IReporteService reporteService;

    @Test
    @DisplayName("01. Debe calcular clasificación ABC de inventario sin errores")
    void testCalcularClasificacionAbc() {
        ClasificacionAbcResponse res = reporteService.calcularClasificacionABC();

        assertNotNull(res);
        assertNotNull(res.getValorTotalInventario());
        assertTrue(res.getValorTotalInventario().compareTo(BigDecimal.ZERO) >= 0);
        assertNotNull(res.getItemsA());
        assertNotNull(res.getItemsB());
        assertNotNull(res.getItemsC());
    }

    @Test
    @DisplayName("02. Los productos categoría A deben tener el mayor porcentaje valorizado")
    void testProductosCategoriaATienenMayorValor() {
        ClasificacionAbcResponse res = reporteService.calcularClasificacionABC();

        if (!res.getItemsA().isEmpty()) {
            ClasificacionAbcResponse.ProductoAbcItem topItem = res.getItemsA().get(0);
            assertEquals("A", topItem.getClasificacion());
            assertNotNull(topItem.getValorValorizado());
        }
    }

    @Test
    @DisplayName("03. El total de productos clasificados debe coincidir con la suma de categorías A, B y C")
    void testSumaDeCategoriasCoincide() {
        ClasificacionAbcResponse res = reporteService.calcularClasificacionABC();

        int suma = res.getItemsA().size() + res.getItemsB().size() + res.getItemsC().size();
        assertEquals(res.getTotalProductos(), suma);
    }
}
