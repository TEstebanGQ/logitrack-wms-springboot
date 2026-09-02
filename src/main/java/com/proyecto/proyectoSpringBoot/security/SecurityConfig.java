package com.proyecto.proyectoSpringBoot.security;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.*;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.*;

import java.util.Arrays;
import java.util.List;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final UserDetailsServiceImpl userDetailsService;
    private final JwtAuthFilter jwtAuthFilter;

    /**
     * Lista de orígenes Cross-Origin Resource Sharing (CORS) autorizados para interactuar con la API RESTful.
     */
    @Value("${app.cors.allowed-origins:http://localhost:3000,http://localhost:8081,http://localhost:8080}")
    private String corsAllowedOriginsRaw;


    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public org.springframework.security.access.hierarchicalroles.RoleHierarchy roleHierarchy() {
        org.springframework.security.access.hierarchicalroles.RoleHierarchyImpl hierarchy = new org.springframework.security.access.hierarchicalroles.RoleHierarchyImpl();
        hierarchy.setHierarchy(
            "ROLE_SUPER_ADMIN > ROLE_ADMIN\n" +
            "ROLE_ADMIN > ROLE_GERENTE_LOGISTICA\n" +
            "ROLE_ADMIN > ROLE_SUPERVISOR\n" +
            "ROLE_GERENTE_LOGISTICA > ROLE_SUPERVISOR\n" +
            "ROLE_SUPERVISOR > ROLE_JEFE_COMPRAS\n" +
            "ROLE_JEFE_COMPRAS > ROLE_EMPLEADO"
        );
        return hierarchy;
    }

    @Bean
    public static org.springframework.security.access.expression.method.DefaultMethodSecurityExpressionHandler methodSecurityExpressionHandler(
            org.springframework.security.access.hierarchicalroles.RoleHierarchy roleHierarchy) {
        org.springframework.security.access.expression.method.DefaultMethodSecurityExpressionHandler handler =
                new org.springframework.security.access.expression.method.DefaultMethodSecurityExpressionHandler();
        handler.setRoleHierarchy(roleHierarchy);
        return handler;
    }

    @Bean
    public DaoAuthenticationProvider authProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(userDetailsService);
        provider.setPasswordEncoder(passwordEncoder());
        return provider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            .csrf(csrf -> csrf.disable())
            .headers(headers -> headers
                .crossOriginOpenerPolicy(coop -> coop.policy(
                    org.springframework.security.web.header.writers.CrossOriginOpenerPolicyHeaderWriter.CrossOriginOpenerPolicy.UNSAFE_NONE
                ))
                .frameOptions(frame -> frame.deny())
            )
            .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                .requestMatchers(
                    "/api/auth/**",
                    "/api/config/**",
                    "/swagger-ui/**",
                    "/v3/api-docs/**",
                    "/swagger-ui.html",
                    "/actuator/**",
                    "/",
                    "/index.html",
                    "/css/**",
                    "/js/**",
                    "/views/**",
                    "/images/**",
                    "/*.svg",
                    "/favicon.ico"
                ).permitAll()
                .requestMatchers("/api/admin/**").hasRole("ADMIN")
                .anyRequest().authenticated()
            )
            .exceptionHandling(ex -> ex
                .authenticationEntryPoint((request, response, authException) -> {
                    response.setStatus(jakarta.servlet.http.HttpServletResponse.SC_UNAUTHORIZED);
                    response.setContentType("application/json;charset=UTF-8");
                    response.getWriter().write("{\"mensaje\":\"Sesión expirada o no autorizada. Inicie sesión nuevamente.\"}");
                })
            )
            .authenticationProvider(authProvider())
            .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOriginPatterns(List.of("*"));
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(List.of("*"));
        config.setAllowCredentials(true);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }
}
