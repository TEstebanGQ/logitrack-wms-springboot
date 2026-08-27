package com.proyecto.proyectoSpringBoot.repository;

import com.proyecto.proyectoSpringBoot.model.entity.AlertaStock;
import com.proyecto.proyectoSpringBoot.model.enums.EstadoAlerta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface AlertaStockRepository extends JpaRepository<AlertaStock, Long> {
    List<AlertaStock> findByEstado(EstadoAlerta estado);
    List<AlertaStock> findByProductoId(Long productoId);
    List<AlertaStock> findByBodegaId(Long bodegaId);
    boolean existsByProductoIdAndBodegaIdAndEstado(Long productoId, Long bodegaId, EstadoAlerta estado);
}
