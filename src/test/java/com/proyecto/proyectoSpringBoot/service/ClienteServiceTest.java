package com.proyecto.proyectoSpringBoot.service;

import com.proyecto.proyectoSpringBoot.dto.request.CrearClienteRequest;
import com.proyecto.proyectoSpringBoot.dto.response.ClienteResponse;
import com.proyecto.proyectoSpringBoot.dto.response.MovimientoResponse;
import com.proyecto.proyectoSpringBoot.exception.ResourceNotFoundException;
import com.proyecto.proyectoSpringBoot.service.interfaces.IClienteService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class ClienteServiceTest {

    @Autowired
    private IClienteService clienteService;

    @Test
    @DisplayName("01. Debe crear un cliente correctamente")
    void testCrearCliente() {
        CrearClienteRequest request = new CrearClienteRequest();
        request.setNombre("Distribuciones Andina S.A.");
        request.setRuc("900999888-1");
        request.setTelefono("3112223344");
        request.setEmail("contacto@andina.co");
        request.setDireccion("Cra 10 # 20-30, Bogotá");

        ClienteResponse response = clienteService.crear(request);

        assertNotNull(response.getId());
        assertEquals("Distribuciones Andina S.A.", response.getNombre());
        assertEquals("900999888-1", response.getRuc());
        assertTrue(response.isActivo());
    }

    @Test
    @DisplayName("02. Debe fallar al crear un cliente con RUC duplicado")
    void testCrearClienteRucDuplicado() {
        CrearClienteRequest req1 = new CrearClienteRequest();
        req1.setNombre("Cliente Original");
        req1.setRuc("900111222-3");
        clienteService.crear(req1);

        CrearClienteRequest req2 = new CrearClienteRequest();
        req2.setNombre("Cliente Duplicado");
        req2.setRuc("900111222-3");

        assertThrows(RuntimeException.class, () -> clienteService.crear(req2));
    }

    @Test
    @DisplayName("03. Debe obtener cliente por ID existente")
    void testObtenerPorId() {
        List<ClienteResponse> lista = clienteService.listar();
        assertFalse(lista.isEmpty(), "Debe haber al menos un cliente en data.sql");
        Long id = lista.get(0).getId();

        ClienteResponse cliente = clienteService.obtenerPorId(id);
        assertNotNull(cliente);
        assertEquals(id, cliente.getId());
    }

    @Test
    @DisplayName("04. Debe lanzar excepción al buscar cliente inexistente")
    void testObtenerInexistente() {
        assertThrows(ResourceNotFoundException.class, () -> clienteService.obtenerPorId(99999L));
    }

    @Test
    @DisplayName("05. Debe listar solo clientes activos")
    void testListarSoloActivos() {
        List<ClienteResponse> activos = clienteService.listar(true);
        assertNotNull(activos);
        assertTrue(activos.stream().allMatch(ClienteResponse::isActivo));
    }

    @Test
    @DisplayName("06. Debe actualizar los datos de un cliente")
    void testActualizarCliente() {
        List<ClienteResponse> lista = clienteService.listar();
        Long id = lista.get(0).getId();

        CrearClienteRequest updateReq = new CrearClienteRequest();
        updateReq.setNombre("Cliente Nombre Actualizado");
        updateReq.setRuc(lista.get(0).getRuc());
        updateReq.setTelefono("3000000000");
        updateReq.setEmail("actualizado@test.com");
        updateReq.setDireccion("Nueva Dirección 123");

        ClienteResponse actualizado = clienteService.actualizar(id, updateReq);
        assertEquals("Cliente Nombre Actualizado", actualizado.getNombre());
        assertEquals("3000000000", actualizado.getTelefono());
    }

    @Test
    @DisplayName("07. Debe realizar soft delete de un cliente")
    void testEliminarCliente() {
        CrearClienteRequest req = new CrearClienteRequest();
        req.setNombre("Cliente Para Eliminar");
        req.setRuc("999888777-9");
        ClienteResponse creado = clienteService.crear(req);

        clienteService.eliminar(creado.getId());

        ClienteResponse buscado = clienteService.obtenerPorId(creado.getId());
        assertFalse(buscado.isActivo());
    }

    @Test
    @DisplayName("08. Debe listar movimientos asociados a un cliente")
    void testListarMovimientosCliente() {
        List<ClienteResponse> lista = clienteService.listar();
        Long id = lista.get(0).getId();

        List<MovimientoResponse> movimientos = clienteService.listarMovimientosCliente(id);
        assertNotNull(movimientos);
    }
}
