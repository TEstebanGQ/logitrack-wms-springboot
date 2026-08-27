package com.proyecto.proyectoSpringBoot.config;

import io.swagger.v3.oas.models.*;
import io.swagger.v3.oas.models.info.*;
import io.swagger.v3.oas.models.security.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("LogiTrack API")
                        .version("1.0.0")
                        .description("Sistema de gestión y auditoría de bodegas - LogiTrack S.A.")
                        .contact(new Contact()
                                .name("LogiTrack Team")
                                .email("soporte@logitrack.com")))
                .addSecurityItem(new SecurityRequirement().addList("bearerAuth"))
                .components(new Components()
                        .addSecuritySchemes("bearerAuth", new SecurityScheme()
                                .name("bearerAuth")
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")
                                .description("Ingresa el token JWT obtenido en /api/auth/login")));
    }
}
