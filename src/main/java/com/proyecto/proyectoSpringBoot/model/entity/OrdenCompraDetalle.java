package com.proyecto.proyectoSpringBoot.model.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "ordenes_compra_detalles")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class OrdenCompraDetalle {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "orden_compra_id", nullable = false)
    private OrdenCompra ordenCompra;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "producto_id", nullable = false)
    private Producto producto;

    @Column(nullable = false)
    private Integer cantidad;

    @Column(name = "precio_unitario", precision = 12, scale = 2, nullable = false)
    private BigDecimal precioUnitario;

    @Column(precision = 12, scale = 2, nullable = false)
    private BigDecimal subtotal;
}
