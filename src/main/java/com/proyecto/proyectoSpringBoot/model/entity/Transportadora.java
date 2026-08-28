package com.proyecto.proyectoSpringBoot.model.entity;

import com.proyecto.proyectoSpringBoot.model.enums.TipoServicioTransporte;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "transportadoras")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Transportadora {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Column(nullable = false, length = 150)
    private String nombre;

    @NotBlank
    @Column(name = "ruc_nit", nullable = false, unique = true, length = 30)
    private String rucNit;

    @Column(length = 30)
    private String telefono;

    @Column(length = 100)
    private String email;

    @Column(length = 100)
    private String contacto;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_servicio", nullable = false, length = 30)
    @Builder.Default
    private TipoServicioTransporte tipoServicio = TipoServicioTransporte.TERRESTRE;

    @Column(nullable = false)
    @Builder.Default
    private Boolean activo = true;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        if (createdAt == null) createdAt = LocalDateTime.now();
        if (activo == null) activo = true;
        if (tipoServicio == null) tipoServicio = TipoServicioTransporte.TERRESTRE;
    }
}
