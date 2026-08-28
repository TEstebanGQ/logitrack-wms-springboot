package com.proyecto.proyectoSpringBoot.repository;

import com.proyecto.proyectoSpringBoot.model.entity.PedidoCliente;
import com.proyecto.proyectoSpringBoot.model.enums.EstadoPedido;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PedidoClienteRepository extends JpaRepository<PedidoCliente, Long> {
    Optional<PedidoCliente> findByCodigoPedido(String codigoPedido);
    List<PedidoCliente> findByClienteId(Long clienteId);
    List<PedidoCliente> findByEstado(EstadoPedido estado);
    List<PedidoCliente> findByBodegaOrigenId(Long bodegaId);
    boolean existsByCodigoPedido(String codigoPedido);
}
