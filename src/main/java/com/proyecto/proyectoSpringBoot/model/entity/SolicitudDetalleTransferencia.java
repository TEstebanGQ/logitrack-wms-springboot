package com.proyecto.proyectoSpringBoot.model.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name="solicitud_detalle_transferencia")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class SolicitudDetalleTransferencia {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "solicitud_id", nullable = false)
    private SolicitudTransferencia solicitud;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "producto_id", nullable = false)
    private Producto producto;

    @Column(nullable = false)
    private Integer cantidad;
}
