package com.proyecto.proyectoSpringBoot.model.entity;

import com.proyecto.proyectoSpringBoot.model.enums.EstadoAlerta;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name="alertas_stock")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class AlertaStock {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "producto_id", nullable = false)
    private Producto producto;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "bodega_id", nullable = false)
    private Bodega bodega;

    @Column(name = "stock_actual", nullable = false)
    private Integer stockActual;

    @Column(name = "stock_minimo", nullable = false)
    private Integer stockMinimo;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private EstadoAlerta estado;

    @Column(name = "fecha_generada", nullable = false, updatable = false)
    private LocalDateTime fechaGenerada;

    @Column(name = "fecha_resuelta")
    private LocalDateTime fechaResuelta;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "resuelta_por_id")
    private Usuario resueltaPor;

    @PrePersist
    protected void onCreate() {
        if (fechaGenerada == null) fechaGenerada = LocalDateTime.now();
        if (estado == null) estado = EstadoAlerta.PENDIENTE;
    }
}
