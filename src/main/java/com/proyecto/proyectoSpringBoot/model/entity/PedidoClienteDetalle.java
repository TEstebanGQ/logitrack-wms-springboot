package com.proyecto.proyectoSpringBoot.model.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "pedido_cliente_detalles")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class PedidoClienteDetalle {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pedido_id", nullable = false)
    private PedidoCliente pedido;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "producto_id", nullable = false)
    private Producto producto;

    @NotNull
    @Positive
    @Column(name = "cantidad_solicitada", nullable = false)
    private Integer cantidadSolicitada;

    @NotNull
    @Column(name = "cantidad_despachada", nullable = false)
    @Builder.Default
    private Integer cantidadDespachada = 0;

    @NotNull
    @Positive
    @Column(name = "precio_unitario", nullable = false, precision = 12, scale = 2)
    private BigDecimal precioUnitario;

    @NotNull
    @Column(nullable = false, precision = 14, scale = 2)
    private BigDecimal subtotal;

    @PrePersist
    @PreUpdate
    protected void calculateSubtotal() {
        if (cantidadDespachada == null) cantidadDespachada = 0;
        if (cantidadSolicitada != null && precioUnitario != null) {
            this.subtotal = precioUnitario.multiply(BigDecimal.valueOf(cantidadSolicitada));
        }
    }
}
