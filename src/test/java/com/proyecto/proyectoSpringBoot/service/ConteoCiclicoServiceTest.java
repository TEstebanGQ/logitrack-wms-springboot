package com.proyecto.proyectoSpringBoot.service;

import com.proyecto.proyectoSpringBoot.dto.request.ConteoCiclicoDetalleRequest;
import com.proyecto.proyectoSpringBoot.dto.request.ConteoCiclicoRequest;
import com.proyecto.proyectoSpringBoot.dto.response.ConteoCiclicoResponse;
import com.proyecto.proyectoSpringBoot.exception.ResourceNotFoundException;
import com.proyecto.proyectoSpringBoot.model.entity.AjusteInventario;
import com.proyecto.proyectoSpringBoot.model.enums.EstadoConteo;
import com.proyecto.proyectoSpringBoot.model.enums.EstadoLineaConteo;
import com.proyecto.proyectoSpringBoot.model.enums.TipoAjuste;
import com.proyecto.proyectoSpringBoot.repository.AjusteInventarioRepository;
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

    @Autowired
    private AjusteInventarioRepository ajusteInventarioRepository;

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

    @Test
    @DisplayName("02. Conciliación con diferencias debe generar AjusteInventario automático")
    void testConteoCiclicoGeneraAjusteAutomatico() {
        ConteoCiclicoRequest req = ConteoCiclicoRequest.builder()
                .bodegaId(1L)
                .fechaProgramada(LocalDate.now())
                .observaciones("Auditoría con discrepancia")
                .detalles(List.of(
                        ConteoCiclicoDetalleRequest.builder()
                                .productoId(2L)
                                .stockFisico(10) // Sistema tiene 15
                                .notas("Faltan 5 unidades")
                                .build()
                ))
                .build();

        ConteoCiclicoResponse creado = conteoCiclicoService.crear(req, "admin@logitrack.com");
        Long detId = creado.getDetalles().get(0).getId();

        conteoCiclicoService.registrarConteoFisico(creado.getId(), detId, 10, "Faltante verificado");
        ConteoCiclicoResponse cerrado = conteoCiclicoService.conciliarYCerrar(creado.getId(), "admin@logitrack.com");

        assertEquals(EstadoConteo.CERRADO, cerrado.getEstado());
        assertEquals(EstadoLineaConteo.AJUSTADO, cerrado.getDetalles().get(0).getEstadoLinea());

        List<AjusteInventario> ajustes = ajusteInventarioRepository.findAll();
        boolean ajusteEncontrado = ajustes.stream()
                .anyMatch(a -> a.getTipoAjuste() == TipoAjuste.CONTEO_FISICO
                        && a.getDiferencia() == -5
                        && a.getCantidadNueva() == 10);

        assertTrue(ajusteEncontrado, "Debe haberse generado un AjusteInventario automático por la discrepancia de -5 unidades");
    }

    @Test
    @DisplayName("03. Debe listar conteos cíclicos por bodega y por estado")
    void testListarPorBodegaYEstado() {
        ConteoCiclicoRequest req = ConteoCiclicoRequest.builder()
                .bodegaId(2L)
                .fechaProgramada(LocalDate.now())
                .observaciones("Auditoría bodega norte")
                .build();

        ConteoCiclicoResponse creado = conteoCiclicoService.crear(req, "admin@logitrack.com");

        List<ConteoCiclicoResponse> porBodega = conteoCiclicoService.listarPorBodega(2L);
        assertFalse(porBodega.isEmpty());
        assertTrue(porBodega.stream().anyMatch(c -> c.getId().equals(creado.getId())));

        List<ConteoCiclicoResponse> porEstado = conteoCiclicoService.listarPorEstado(EstadoConteo.PROGRAMADO);
        assertFalse(porEstado.isEmpty());
        assertTrue(porEstado.stream().anyMatch(c -> c.getId().equals(creado.getId())));
    }

    @Test
    @DisplayName("04. Debe lanzar ResourceNotFoundException si el conteo cíclico no existe")
    void testConteoNotFound() {
        assertThrows(ResourceNotFoundException.class, () -> conteoCiclicoService.obtenerPorId(999999L));
    }
}
