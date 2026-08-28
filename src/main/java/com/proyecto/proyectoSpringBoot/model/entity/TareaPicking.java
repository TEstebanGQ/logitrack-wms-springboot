package com.proyecto.proyectoSpringBoot.model.entity;

import com.proyecto.proyectoSpringBoot.model.enums.EstadoPicking;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "tareas_picking")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class TareaPicking {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Column(name = "codigo_tarea", nullable = false, unique = true, length = 50)
    private String codigoTarea;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pedido_id", nullable = false)
    private PedidoCliente pedido;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "producto_id", nullable = false)
    private Producto producto;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ubicacion_origen_id")
    private UbicacionBodega ubicacionOrigen;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_asignado_id")
    private Usuario usuarioAsignado;

    @NotNull
    @Positive
    @Column(name = "cantidad_requerida", nullable = false)
    private Integer cantidadRequerida;

    @NotNull
    @Column(name = "cantidad_recogida", nullable = false)
    @Builder.Default
    private Integer cantidadRecogida = 0;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    @Builder.Default
    private EstadoPicking estado = EstadoPicking.PENDIENTE;

    @Column(name = "fecha_asignacion", nullable = false)
    private LocalDateTime fechaAsignacion;

    @Column(name = "fecha_completada")
    private LocalDateTime fechaCompletada;

    @Column(length = 300)
    private String notas;

    @PrePersist
    protected void onCreate() {
        if (fechaAsignacion == null) fechaAsignacion = LocalDateTime.now();
        if (estado == null) estado = EstadoPicking.PENDIENTE;
        if (cantidadRecogida == null) cantidadRecogida = 0;
    }
}
