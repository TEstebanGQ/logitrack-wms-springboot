package com.proyecto.proyectoSpringBoot.controller;

import com.proyecto.proyectoSpringBoot.model.entity.Usuario;
import com.proyecto.proyectoSpringBoot.model.enums.RolUsuario;
import com.proyecto.proyectoSpringBoot.service.interfaces.IEmailService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class AuthEmailIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private IEmailService emailService;

    @Test
    @DisplayName("01. POST /api/auth/register debe registrar usuario y disparar correo de bienvenida")
    void testRegisterTriggersWelcomeEmail() throws Exception {
        String json = """
                {
                    "nombre": "Esteban",
                    "apellido": "Gómez",
                    "email": "esteban.logistica@empresa.com",
                    "password": "Password123*",
                    "rol": "GERENTE_LOGISTICA"
                }
                """;

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.mensaje").value("Usuario registrado exitosamente"));

        verify(emailService, times(1)).enviarCorreoBienvenida(any(Usuario.class));
    }
}
