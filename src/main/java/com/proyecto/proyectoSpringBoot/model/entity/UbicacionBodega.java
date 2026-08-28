package com.proyecto.proyectoSpringBoot.model.entity;

import com.proyecto.proyectoSpringBoot.listener.AuditoriaEntityListener;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "ubicaciones_bodega", uniqueConstraints = @UniqueConstraint(columnNames = {"bodega_id", "codigo_ubicacion"}))
@EntityListeners(AuditoriaEntityListener.class)
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class UbicacionBodega {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "bodega_id", nullable = false)
    private Bodega bodega;

    @Column(name = "codigo_ubicacion", length = 50, nullable = false)
    private String codigoUbicacion;

    @Column(length = 30, nullable = false)
    private String pasillo;

    @Column(length = 30, nullable = false)
    private String estante;

    @Column(length = 30, nullable = false)
    private String nivel;

    @Column(name = "capacidad_max", nullable = false)
    @Builder.Default
    private Integer capacidadMax = 100;

    @Column(length = 200)
    private String descripcion;

    @Column(nullable = false)
    @Builder.Default
    private Boolean activo = true;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
        if (activo == null) {
            activo = true;
        }
    }
}
