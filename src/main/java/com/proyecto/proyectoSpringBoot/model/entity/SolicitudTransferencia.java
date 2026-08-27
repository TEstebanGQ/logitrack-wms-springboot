package com.proyecto.proyectoSpringBoot.model.entity;

import com.proyecto.proyectoSpringBoot.model.enums.EstadoSolicitud;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name="solicitudes_transferencia")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class SolicitudTransferencia {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "bodega_origen_id", nullable = false)
    private Bodega bodegaOrigen;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "bodega_destino_id", nullable = false)
    private Bodega bodegaDestino;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "solicitante_id", nullable = false)
    private Usuario solicitante;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "aprobador_id")
    private Usuario aprobador;

    @Enumerated(EnumType.STRING)
    @Column(length = 30, nullable = false)
    private EstadoSolicitud estado;

    @Column(length = 500)
    private String observaciones;

    @Column(name = "motivo_rechazo", length = 500)
    private String motivoRechazo;

    @Column(name = "total_unidades", nullable = false)
    private Integer totalUnidades;

    @Column(name = "fecha_solicitud", nullable = false, updatable = false)
    private LocalDateTime fechaSolicitud;

    @Column(name = "fecha_resolucion")
    private LocalDateTime fechaResolucion;

    @Column(name = "fecha_expiracion")
    private LocalDateTime fechaExpiracion;

    @Builder.Default
    @OneToMany(mappedBy="solicitud", cascade=CascadeType.ALL, orphanRemoval=true)
    private List<SolicitudDetalleTransferencia> detalles = new ArrayList<>();

    @PrePersist
    protected void onCreate() {
        if (fechaSolicitud == null) fechaSolicitud = LocalDateTime.now();
        if (estado == null) estado = EstadoSolicitud.PENDIENTE;
        if (fechaExpiracion == null) fechaExpiracion = fechaSolicitud.plusHours(48);
    }
}
