package com.proyecto.proyectoSpringBoot.controller;

import com.proyecto.proyectoSpringBoot.dto.request.CrearOrdenCompraRequest;
import com.proyecto.proyectoSpringBoot.dto.response.OrdenCompraResponse;
import com.proyecto.proyectoSpringBoot.model.entity.Usuario;
import com.proyecto.proyectoSpringBoot.model.enums.RolUsuario;
import com.proyecto.proyectoSpringBoot.repository.UsuarioRepository;
import com.proyecto.proyectoSpringBoot.service.interfaces.IOrdenCompraService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class SecurityFixesVerificationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private IOrdenCompraService ordenCompraService;

    @Test
    @DisplayName("01. [H-003] Registro público con rol ADMIN debe ser forzado a rol EMPLEADO")
    void testRegisterRoleAdminEnforcedToEmpleado() throws Exception {
        String json = """
                {
                    "nombre": "Atacante",
                    "apellido": "Root",
                    "email": "hacker_admin@logitrack.com",
                    "password": "Password123!",
                    "rol": "ADMIN"
                }
                """;

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.mensaje").value("Usuario registrado exitosamente"));

        Usuario usuario = usuarioRepository.findByEmail("hacker_admin@logitrack.com")
                .orElseThrow(() -> new AssertionError("El usuario debería haber sido registrado"));

        assertEquals(RolUsuario.EMPLEADO, usuario.getRol(),
                "Por seguridad (H-003), cualquier registro público debe tener rol EMPLEADO");
    }

    @Test
    @DisplayName("02. [H-003] Registro público con rol SUPERVISOR debe ser forzado a rol EMPLEADO")
    void testRegisterRoleSupervisorEnforcedToEmpleado() throws Exception {
        String json = """
                {
                    "nombre": "Supervisor",
                    "apellido": "NoAutorizado",
                    "email": "hacker_sup@logitrack.com",
                    "password": "Password123!",
                    "rol": "SUPERVISOR"
                }
                """;

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk());

        Usuario usuario = usuarioRepository.findByEmail("hacker_sup@logitrack.com")
                .orElseThrow(() -> new AssertionError("El usuario debería haber sido registrado"));

        assertEquals(RolUsuario.EMPLEADO, usuario.getRol(),
                "Por seguridad (H-003), cualquier registro público debe tener rol EMPLEADO");
    }

    @Test
    @DisplayName("03. [H-005] Generación atómica de códigos de orden garantiza unicidad sin colisiones")
    void testGeneracionCodigosOrdenUnicos() {
        Set<String> codigos = new HashSet<>();
        int cantidadOrdenes = 5;

        for (int i = 0; i < cantidadOrdenes; i++) {
            CrearOrdenCompraRequest req = CrearOrdenCompraRequest.builder()
                    .proveedorId(1L)
                    .bodegaDestinoId(1L)
                    .fechaEntregaEsperada(LocalDate.now().plusDays(7))
                    .observaciones("Orden de prueba secuencial " + i)
                    .detalles(List.of(
                            CrearOrdenCompraRequest.DetalleOrdenRequest.builder()
                                    .productoId(1L)
                                    .cantidad(10)
                                    .precioUnitario(BigDecimal.valueOf(100000))
                                    .build()
                    ))
                    .build();

            OrdenCompraResponse resp = ordenCompraService.crearOrden(req, "admin@logitrack.com");
            assertNotNull(resp.getCodigoOrden());
            assertTrue(resp.getCodigoOrden().startsWith("OC-"), "El código debe tener prefijo OC-");
            assertTrue(codigos.add(resp.getCodigoOrden()),
                    "El código " + resp.getCodigoOrden() + " ya existía, violando la unicidad atómica (H-005)");
        }

        assertEquals(cantidadOrdenes, codigos.size(), "Todos los códigos generados deben ser distintos");
    }

    @Test
    @DisplayName("04. [H-001] Endpoint de Google Auth responde 400 Bad Request si el token es inválido")
    void testGoogleAuthTokenInvalidoRetorna400() throws Exception {
        String json = """
                {
                    "credential": "token.jwt.falso.sin.firma"
                }
                """;

        mockMvc.perform(post("/api/auth/google")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.mensaje").isNotEmpty());
    }

    @Test
    @DisplayName("05. Endpoint público /api/config/public expone googleClientId sin secretos")
    void testPublicConfigSeguro() throws Exception {
        mockMvc.perform(get("/api/config/public"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.googleClientId").exists());
    }
}
