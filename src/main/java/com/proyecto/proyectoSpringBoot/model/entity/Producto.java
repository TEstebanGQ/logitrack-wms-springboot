package com.proyecto.proyectoSpringBoot.model.entity;

import com.proyecto.proyectoSpringBoot.listener.AuditoriaEntityListener;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "productos")
@EntityListeners(AuditoriaEntityListener.class)
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
@com.fasterxml.jackson.annotation.JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Producto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Size(max = 150)
    @Column(nullable = false, length = 150)
    private String nombre;

    @NotNull(message = "La categoría es obligatoria")
    @com.fasterxml.jackson.annotation.JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "categoria_id", nullable = false)
    private Categoria categoria;

    @Builder.Default
    @Min(0)
    @Column(nullable = false)
    private Integer stock = 0;

    @Builder.Default
    @Column(name="stock_minimo", nullable=false)
    private Integer stockMinimo = 10;

    @DecimalMin("0.0")
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal precio;

    @Size(max = 500)
    @Column(length = 500)
    private String descripcion;

    @Builder.Default
    @Column(nullable = false)
    private boolean activo = true;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @com.fasterxml.jackson.annotation.JsonIgnore
    @OneToMany(mappedBy = "producto", cascade = CascadeType.ALL)
    private List<MovimientoDetalle> detalles;

    @com.fasterxml.jackson.annotation.JsonIgnore
    @OneToMany(mappedBy = "producto", cascade = CascadeType.ALL)
    private List<InventarioBodega> inventarios;

    @PrePersist
    protected void onCreate() { createdAt = LocalDateTime.now(); }

    @PreUpdate
    protected void onUpdate() { updatedAt = LocalDateTime.now(); }
}
