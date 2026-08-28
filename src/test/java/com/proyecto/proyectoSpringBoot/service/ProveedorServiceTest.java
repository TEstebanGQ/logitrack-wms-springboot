package com.proyecto.proyectoSpringBoot.service;

import com.proyecto.proyectoSpringBoot.dto.request.CrearProveedorRequest;
import com.proyecto.proyectoSpringBoot.dto.response.ProveedorResponse;
import com.proyecto.proyectoSpringBoot.dto.response.MovimientoResponse;
import com.proyecto.proyectoSpringBoot.exception.ResourceNotFoundException;
import com.proyecto.proyectoSpringBoot.service.interfaces.IProveedorService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class ProveedorServiceTest {

    @Autowired
    private IProveedorService proveedorService;

    @Test
    @DisplayName("01. Debe crear un proveedor correctamente")
    void testCrearProveedor() {
        CrearProveedorRequest request = new CrearProveedorRequest();
        request.setNombre("Tech Global Imports S.A.S.");
        request.setRuc("901555666-4");
        request.setTelefono("3159998877");
        request.setEmail("ventas@techglobal.co");
        request.setDireccion("Zona Franca Fontibón, Bogotá");

        ProveedorResponse response = proveedorService.crear(request);

        assertNotNull(response.getId());
        assertEquals("Tech Global Imports S.A.S.", response.getNombre());
        assertEquals("901555666-4", response.getRuc());
        assertTrue(response.isActivo());
    }

    @Test
    @DisplayName("02. Debe fallar al crear un proveedor con RUC duplicado")
    void testCrearProveedorRucDuplicado() {
        CrearProveedorRequest req1 = new CrearProveedorRequest();
        req1.setNombre("Proveedor Original");
        req1.setRuc("900333444-5");
        proveedorService.crear(req1);

        CrearProveedorRequest req2 = new CrearProveedorRequest();
        req2.setNombre("Proveedor Duplicado");
        req2.setRuc("900333444-5");

        assertThrows(RuntimeException.class, () -> proveedorService.crear(req2));
    }

    @Test
    @DisplayName("03. Debe obtener proveedor por ID existente")
    void testObtenerPorId() {
        List<ProveedorResponse> lista = proveedorService.listar();
        assertFalse(lista.isEmpty(), "Debe haber al menos un proveedor en data.sql");
        Long id = lista.get(0).getId();

        ProveedorResponse proveedor = proveedorService.obtenerPorId(id);
        assertNotNull(proveedor);
        assertEquals(id, proveedor.getId());
    }

    @Test
    @DisplayName("04. Debe lanzar excepción al buscar proveedor inexistente")
    void testObtenerInexistente() {
        assertThrows(ResourceNotFoundException.class, () -> proveedorService.obtenerPorId(99999L));
    }

    @Test
    @DisplayName("05. Debe listar solo proveedores activos")
    void testListarSoloActivos() {
        List<ProveedorResponse> activos = proveedorService.listar(true);
        assertNotNull(activos);
        assertTrue(activos.stream().allMatch(ProveedorResponse::isActivo));
    }

    @Test
    @DisplayName("06. Debe actualizar los datos de un proveedor")
    void testActualizarProveedor() {
        List<ProveedorResponse> lista = proveedorService.listar();
        Long id = lista.get(0).getId();

        CrearProveedorRequest updateReq = new CrearProveedorRequest();
        updateReq.setNombre("Proveedor Modificado");
        updateReq.setRuc(lista.get(0).getRuc());
        updateReq.setTelefono("3110001122");
        updateReq.setEmail("actualizado@proveedor.com");
        updateReq.setDireccion("Avenida Siempre Viva 742");

        ProveedorResponse actualizado = proveedorService.actualizar(id, updateReq);
        assertEquals("Proveedor Modificado", actualizado.getNombre());
        assertEquals("3110001122", actualizado.getTelefono());
    }

    @Test
    @DisplayName("07. Debe realizar soft delete de un proveedor")
    void testEliminarProveedor() {
        CrearProveedorRequest req = new CrearProveedorRequest();
        req.setNombre("Proveedor Para Eliminar");
        req.setRuc("999111222-8");
        ProveedorResponse creado = proveedorService.crear(req);

        proveedorService.eliminar(creado.getId());

        ProveedorResponse buscado = proveedorService.obtenerPorId(creado.getId());
        assertFalse(buscado.isActivo());
    }

    @Test
    @DisplayName("08. Debe listar movimientos de suministro asociados a un proveedor")
    void testListarMovimientosProveedor() {
        List<ProveedorResponse> lista = proveedorService.listar();
        Long id = lista.get(0).getId();

        List<MovimientoResponse> movimientos = proveedorService.listarMovimientosProveedor(id);
        assertNotNull(movimientos);
    }
}
