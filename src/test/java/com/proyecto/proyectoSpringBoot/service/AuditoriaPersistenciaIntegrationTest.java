package com.proyecto.proyectoSpringBoot.service;

import com.proyecto.proyectoSpringBoot.dto.request.CrearAjusteRequest;
import com.proyecto.proyectoSpringBoot.dto.request.MovimientoRequest;
import com.proyecto.proyectoSpringBoot.model.entity.Auditoria;
import com.proyecto.proyectoSpringBoot.model.enums.TipoAjuste;
import com.proyecto.proyectoSpringBoot.model.enums.TipoMovimiento;
import com.proyecto.proyectoSpringBoot.model.enums.TipoOperacion;
import com.proyecto.proyectoSpringBoot.repository.AuditoriaRepository;
import com.proyecto.proyectoSpringBoot.service.interfaces.IAjusteInventarioService;
import com.proyecto.proyectoSpringBoot.service.interfaces.IMovimientoService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class AuditoriaPersistenciaIntegrationTest {

    @Autowired
    private IMovimientoService movimientoService;

    @Autowired
    private IAjusteInventarioService ajusteInventarioService;

    @Autowired
    private AuditoriaRepository auditoriaRepository;

    @Test
    @DisplayName("01. Registrar movimiento ENTRADA debe generar registro de auditoría con tipo INSERT")
    void testAuditoriaGeneradaEnMovimiento() {
        MovimientoRequest req = MovimientoRequest.builder()
                .tipoMovimiento(TipoMovimiento.ENTRADA)
                .bodegaDestinoId(1L)
                .observaciones("Entrada auditada de prueba")
                .detalles(List.of(
                        MovimientoRequest.DetalleRequest.builder()
                                .productoId(1L)
                                .cantidad(5)
                                .build()
                ))
                .build();

        movimientoService.registrar(req, "admin@logitrack.com");

        List<Auditoria> auditorias = auditoriaRepository.findAll();
        boolean encontrada = auditorias.stream()
                .anyMatch(a -> "Movimiento".equalsIgnoreCase(a.getEntidad())
                        && a.getTipoOperacion() == TipoOperacion.INSERT
                        && a.getDescripcion() != null
                        && a.getDescripcion().contains("ENTRADA"));

        assertTrue(encontrada, "Debe existir un registro de auditoría para el movimiento registrado");
    }

    @Test
    @DisplayName("02. Registrar ajuste de inventario debe generar registro de auditoría")
    void testAuditoriaGeneradaEnAjuste() {
        CrearAjusteRequest req = CrearAjusteRequest.builder()
                .bodegaId(1L)
                .productoId(1L)
                .tipoAjuste(TipoAjuste.MERMA)
                .cantidadNueva(18)
                .justificacion("Ajuste por daño en empaque")
                .build();

        ajusteInventarioService.registrarAjuste(req, "admin@logitrack.com");

        List<Auditoria> auditorias = auditoriaRepository.findAll();
        boolean encontrada = auditorias.stream()
                .anyMatch(a -> "AjusteInventario".equalsIgnoreCase(a.getEntidad())
                        && a.getTipoOperacion() == TipoOperacion.INSERT);

        assertTrue(encontrada, "Debe existir un registro de auditoría para el ajuste registrado");
    }
}
