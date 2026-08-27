package com.proyecto.proyectoSpringBoot.service;

import com.proyecto.proyectoSpringBoot.dto.response.AuditoriaResponse;
import com.proyecto.proyectoSpringBoot.model.enums.TipoOperacion;
import com.proyecto.proyectoSpringBoot.service.interfaces.IAuditoriaService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class AuditoriaServiceTest {

    @Autowired
    private IAuditoriaService auditoriaService;

    @Test
    @DisplayName("27. Debe listar todas las auditorías registradas")
    void testListarTodasLasAuditorias() {
        List<AuditoriaResponse> auditorias = auditoriaService.listarTodas();
        assertNotNull(auditorias);
    }

    @Test
    @DisplayName("28. Debe filtrar auditorías por entidad afectada")
    void testListarPorEntidad() {
        List<AuditoriaResponse> bodegas = auditoriaService.listarPorEntidad("Bodega");
        assertNotNull(bodegas);
    }

    @Test
    @DisplayName("29. Debe filtrar auditorías por tipo de operación INSERT")
    void testListarPorTipoOperacionInsert() {
        List<AuditoriaResponse> inserts = auditoriaService.listarPorTipoOperacion(TipoOperacion.INSERT);
        assertNotNull(inserts);
    }

    @Test
    @DisplayName("30. Debe filtrar auditorías por tipo de operación UPDATE")
    void testListarPorTipoOperacionUpdate() {
        List<AuditoriaResponse> updates = auditoriaService.listarPorTipoOperacion(TipoOperacion.UPDATE);
        assertNotNull(updates);
    }

    @Test
    @DisplayName("31. Debe filtrar auditorías por rango de fechas")
    void testListarPorRangoFechas() {
        LocalDateTime inicio = LocalDateTime.now().minusDays(30);
        LocalDateTime fin = LocalDateTime.now().plusDays(1);

        List<AuditoriaResponse> rango = auditoriaService.listarPorRangoFechas(inicio, fin);
        assertNotNull(rango);
    }
}
