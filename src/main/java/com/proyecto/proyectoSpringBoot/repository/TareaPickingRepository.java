package com.proyecto.proyectoSpringBoot.repository;

import com.proyecto.proyectoSpringBoot.model.entity.TareaPicking;
import com.proyecto.proyectoSpringBoot.model.enums.EstadoPicking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TareaPickingRepository extends JpaRepository<TareaPicking, Long> {
    Optional<TareaPicking> findByCodigoTarea(String codigoTarea);
    List<TareaPicking> findByPedidoId(Long pedidoId);
    List<TareaPicking> findByUsuarioAsignadoId(Long usuarioId);
    List<TareaPicking> findByEstado(EstadoPicking estado);
}
