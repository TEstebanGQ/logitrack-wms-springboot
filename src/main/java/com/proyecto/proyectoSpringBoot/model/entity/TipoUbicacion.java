package com.proyecto.proyectoSpringBoot.model.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "tipos_ubicacion")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class TipoUbicacion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Column(nullable = false, unique = true, length = 30)
    private String codigo;

    @NotBlank
    @Column(nullable = false, length = 100)
    private String nombre;

    @NotNull
    @Column(name = "peso_maximo_kg", nullable = false)
    @Builder.Default
    private Double pesoMaximoKg = 1000.0;

    @NotNull
    @Column(name = "volumen_maximo_m3", nullable = false)
    @Builder.Default
    private Double volumenMaximoM3 = 2.5;

    @Column(length = 255)
    private String descripcion;

    @Column(nullable = false)
    @Builder.Default
    private Boolean activo = true;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        if (createdAt == null) createdAt = LocalDateTime.now();
        if (activo == null) activo = true;
        if (pesoMaximoKg == null) pesoMaximoKg = 1000.0;
        if (volumenMaximoM3 == null) volumenMaximoM3 = 2.5;
    }
}
