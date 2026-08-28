package com.proyecto.proyectoSpringBoot.repository;

import com.proyecto.proyectoSpringBoot.model.entity.PedidoClienteDetalle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PedidoClienteDetalleRepository extends JpaRepository<PedidoClienteDetalle, Long> {
    List<PedidoClienteDetalle> findByPedidoId(Long pedidoId);
}
