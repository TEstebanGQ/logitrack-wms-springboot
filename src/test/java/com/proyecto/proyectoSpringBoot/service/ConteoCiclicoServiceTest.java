package com.proyecto.proyectoSpringBoot.service;

import com.proyecto.proyectoSpringBoot.dto.request.ConteoCiclicoDetalleRequest;
import com.proyecto.proyectoSpringBoot.dto.request.ConteoCiclicoRequest;
import com.proyecto.proyectoSpringBoot.dto.response.ConteoCiclicoResponse;
import com.proyecto.proyectoSpringBoot.model.enums.EstadoConteo;
import com.proyecto.proyectoSpringBoot.service.interfaces.IConteoCiclicoService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class ConteoCiclicoServiceTest {

    @Autowired
    private IConteoCiclicoService conteoCiclicoService;

    @Test
    @DisplayName("01. Debe crear un conteo cíclico, registrar conteo físico y conciliar")
    void testConteoCiclicoLifecycle() {
        ConteoCiclicoRequest req = ConteoCiclicoRequest.builder()
                .bodegaId(1L)
                .fechaProgramada(LocalDate.now())
                .observaciones("Auditoría mensual pasillo 1")
                .detalles(List.of(
                        ConteoCiclicoDetalleRequest.builder()
                                .productoId(1L)
                                .stockFisico(20)
                                .notas("Conteo inicial")
                                .build()
                ))
                .build();

        ConteoCiclicoResponse creado = conteoCiclicoService.crear(req, "admin@logitrack.com");
        assertNotNull(creado);
        assertEquals(EstadoConteo.PROGRAMADO, creado.getEstado());

        Long detId = creado.getDetalles().get(0).getId();
        ConteoCiclicoResponse contado = conteoCiclicoService.registrarConteoFisico(creado.getId(), detId, 18, "Se encontraron 18 unidades");
        assertEquals(EstadoConteo.EN_PROCESO, contado.getEstado());

        ConteoCiclicoResponse cerrado = conteoCiclicoService.conciliarYCerrar(creado.getId(), "admin@logitrack.com");
        assertEquals(EstadoConteo.CERRADO, cerrado.getEstado());
    }
}
