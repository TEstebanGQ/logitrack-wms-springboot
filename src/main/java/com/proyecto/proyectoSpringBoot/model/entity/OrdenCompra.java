package com.proyecto.proyectoSpringBoot.model.entity;

import com.proyecto.proyectoSpringBoot.listener.AuditoriaEntityListener;
import com.proyecto.proyectoSpringBoot.model.enums.EstadoOrdenCompra;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "ordenes_compra")
@EntityListeners(AuditoriaEntityListener.class)
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class OrdenCompra {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "codigo_orden", unique = true, nullable = false, length = 50)
    private String codigoOrden;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "proveedor_id", nullable = false)
    private Proveedor proveedor;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "bodega_destino_id", nullable = false)
    private Bodega bodegaDestino;

    @Enumerated(EnumType.STRING)
    @Column(length = 30, nullable = false)
    @Builder.Default
    private EstadoOrdenCompra estado = EstadoOrdenCompra.PENDIENTE;

    @Column(name = "total_estimado", precision = 14, scale = 2, nullable = false)
    @Builder.Default
    private BigDecimal totalEstimado = BigDecimal.ZERO;

    @Column(name = "fecha_solicitud", nullable = false)
    private LocalDateTime fechaSolicitud;

    @Column(name = "fecha_entrega_esperada")
    private LocalDate fechaEntregaEsperada;

    @Column(name = "observaciones", length = 500)
    private String observaciones;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_solicitante_id", nullable = false)
    private Usuario usuarioSolicitante;

    @OneToMany(mappedBy = "ordenCompra", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<OrdenCompraDetalle> detalles = new ArrayList<>();

    @PrePersist
    protected void onCreate() {
        if (fechaSolicitud == null) {
            fechaSolicitud = LocalDateTime.now();
        }
        if (estado == null) {
            estado = EstadoOrdenCompra.PENDIENTE;
        }
    }
}
