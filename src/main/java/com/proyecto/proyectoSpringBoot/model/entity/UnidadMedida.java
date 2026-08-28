package com.proyecto.proyectoSpringBoot.model.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "unidades_medida")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class UnidadMedida {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Column(nullable = false, unique = true, length = 20)
    private String codigo;

    @NotBlank
    @Column(nullable = false, length = 100)
    private String nombre;

    @Column(length = 20)
    private String abreviatura;

    @NotNull
    @Column(name = "factor_conversion", nullable = false)
    @Builder.Default
    private Double factorConversion = 1.0;

    @Column(nullable = false)
    @Builder.Default
    private Boolean activo = true;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        if (createdAt == null) createdAt = LocalDateTime.now();
        if (activo == null) activo = true;
        if (factorConversion == null) factorConversion = 1.0;
    }
}
