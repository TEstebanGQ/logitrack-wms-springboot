package com.proyecto.proyectoSpringBoot.model.entity;

import com.proyecto.proyectoSpringBoot.model.enums.EstadoLineaConteo;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Entity
@Table(name = "conteo_ciclico_detalles")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class ConteoCiclicoDetalle {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "conteo_id", nullable = false)
    private ConteoCiclico conteo;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "producto_id", nullable = false)
    private Producto producto;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ubicacion_id")
    private UbicacionBodega ubicacion;

    @NotNull
    @Column(name = "stock_sistema", nullable = false)
    private Integer stockSistema;

    @Column(name = "stock_fisico")
    private Integer stockFisico;

    @Column(nullable = false)
    @Builder.Default
    private Integer diferencia = 0;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "estado_linea", nullable = false, length = 30)
    @Builder.Default
    private EstadoLineaConteo estadoLinea = EstadoLineaConteo.PENDIENTE;

    @Column(length = 255)
    private String notas;

    @PrePersist
    @PreUpdate
    protected void onCalculateDiff() {
        if (stockFisico != null && stockSistema != null) {
            this.diferencia = this.stockFisico - this.stockSistema;
        } else {
            this.diferencia = 0;
        }
        if (estadoLinea == null) estadoLinea = EstadoLineaConteo.PENDIENTE;
    }
}
