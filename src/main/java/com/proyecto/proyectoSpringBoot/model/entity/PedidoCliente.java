package com.proyecto.proyectoSpringBoot.model.entity;

import com.proyecto.proyectoSpringBoot.model.enums.EstadoPedido;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "pedidos_cliente")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class PedidoCliente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Column(name = "codigo_pedido", nullable = false, unique = true, length = 50)
    private String codigoPedido;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cliente_id", nullable = false)
    private Cliente cliente;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "bodega_origen_id", nullable = false)
    private Bodega bodegaOrigen;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    @Builder.Default
    private EstadoPedido estado = EstadoPedido.PENDIENTE;

    @NotNull
    @Column(name = "total_pedido", nullable = false, precision = 14, scale = 2)
    @Builder.Default
    private BigDecimal totalPedido = BigDecimal.ZERO;

    @Column(name = "fecha_pedido", nullable = false)
    private LocalDateTime fechaPedido;

    @Column(name = "fecha_compromiso")
    private LocalDate fechaCompromiso;

    @Column(name = "direccion_entrega", length = 300)
    private String direccionEntrega;

    @Column(length = 500)
    private String observaciones;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_creador_id", nullable = false)
    private Usuario usuarioCreador;

    @OneToMany(mappedBy = "pedido", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<PedidoClienteDetalle> detalles = new ArrayList<>();

    @PrePersist
    protected void onCreate() {
        if (fechaPedido == null) fechaPedido = LocalDateTime.now();
        if (estado == null) estado = EstadoPedido.PENDIENTE;
        if (totalPedido == null) totalPedido = BigDecimal.ZERO;
    }
}
