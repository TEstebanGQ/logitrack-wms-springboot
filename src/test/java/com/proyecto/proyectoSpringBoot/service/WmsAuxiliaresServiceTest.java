package com.proyecto.proyectoSpringBoot.service;

import com.proyecto.proyectoSpringBoot.dto.request.ProductoSerieRequest;
import com.proyecto.proyectoSpringBoot.dto.request.UnidadMedidaRequest;
import com.proyecto.proyectoSpringBoot.dto.request.ZonaBodegaRequest;
import com.proyecto.proyectoSpringBoot.dto.response.ProductoSerieResponse;
import com.proyecto.proyectoSpringBoot.dto.response.UnidadMedidaResponse;
import com.proyecto.proyectoSpringBoot.dto.response.ZonaBodegaResponse;
import com.proyecto.proyectoSpringBoot.model.enums.EstadoSerie;
import com.proyecto.proyectoSpringBoot.model.enums.TipoZona;
import com.proyecto.proyectoSpringBoot.service.interfaces.IProductoSerieService;
import com.proyecto.proyectoSpringBoot.service.interfaces.IUnidadMedidaService;
import com.proyecto.proyectoSpringBoot.service.interfaces.IZonaBodegaService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class WmsAuxiliaresServiceTest {

    @Autowired
    private IUnidadMedidaService unidadMedidaService;

    @Autowired
    private IProductoSerieService productoSerieService;

    @Autowired
    private IZonaBodegaService zonaBodegaService;

    @Test
    @DisplayName("01. Debe crear y listar unidades de medida")
    void testUnidadesMedida() {
        UnidadMedidaRequest req = UnidadMedidaRequest.builder()
                .codigo("PLT")
                .nombre("Palet Industrial")
                .abreviatura("PLT")
                .factorConversion(40.0)
                .activo(true)
                .build();
        UnidadMedidaResponse res = unidadMedidaService.crear(req);
        assertNotNull(res);
        assertEquals("PLT", res.getCodigo());

        List<UnidadMedidaResponse> activas = unidadMedidaService.listarActivas();
        assertFalse(activas.isEmpty());
    }

    @Test
    @DisplayName("02. Debe registrar y actualizar números de serie")
    void testProductoSeries() {
        ProductoSerieRequest req = ProductoSerieRequest.builder()
                .numeroSerie("SN-DELL-XYZ-" + System.currentTimeMillis())
                .productoId(1L)
                .bodegaId(1L)
                .estado(EstadoSerie.EN_STOCK)
                .build();
        ProductoSerieResponse res = productoSerieService.registrar(req);
        assertNotNull(res);
        assertEquals(EstadoSerie.EN_STOCK, res.getEstado());

        ProductoSerieResponse despachado = productoSerieService.actualizarEstado(res.getId(), EstadoSerie.DESPACHADO, "Enviado a cliente");
        assertEquals(EstadoSerie.DESPACHADO, despachado.getEstado());
    }

    @Test
    @DisplayName("03. Debe crear y listar zonas de bodega")
    void testZonasBodega() {
        ZonaBodegaRequest req = ZonaBodegaRequest.builder()
                .codigo("ZN-REC-01")
                .nombre("Zona Recepción Principal")
                .tipoZona(TipoZona.RECEPCION)
                .bodegaId(1L)
                .temperaturaControlada(false)
                .activo(true)
                .build();
        ZonaBodegaResponse res = zonaBodegaService.crear(req);
        assertNotNull(res);
        assertEquals(TipoZona.RECEPCION, res.getTipoZona());
    }
}
