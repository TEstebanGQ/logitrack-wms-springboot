package com.proyecto.proyectoSpringBoot.model.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "bodegas")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Bodega {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Size(max = 150)
    @Column(nullable = false, length = 150)
    private String nombre;

    @NotBlank
    @Size(max = 255)
    @Column(nullable = false)
    private String ubicacion;

    @Min(1)
    @Column(nullable = false)
    private Integer capacidad;

    @NotBlank
    @Size(max = 150)
    @Column(nullable = false, length = 150)
    private String encargado;

    @Column(nullable = false)
    private boolean activo = true;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "bodegaOrigen")
    private List<Movimiento> movimientosOrigen;

    @OneToMany(mappedBy = "bodegaDestino")
    private List<Movimiento> movimientosDestino;

    @OneToMany(mappedBy = "bodega", cascade = CascadeType.ALL)
    private List<InventarioBodega> inventarios;

    @PrePersist
    protected void onCreate() { createdAt = LocalDateTime.now(); }

    @PreUpdate
    protected void onUpdate() { updatedAt = LocalDateTime.now(); }
}
