package com.proyecto.proyectoSpringBoot.repository;

import com.proyecto.proyectoSpringBoot.model.entity.ProductoSerie;
import com.proyecto.proyectoSpringBoot.model.enums.EstadoSerie;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductoSerieRepository extends JpaRepository<ProductoSerie, Long> {
    Optional<ProductoSerie> findByNumeroSerie(String numeroSerie);
    List<ProductoSerie> findByProductoId(Long productoId);
    List<ProductoSerie> findByBodegaId(Long bodegaId);
    List<ProductoSerie> findByEstado(EstadoSerie estado);
    boolean existsByNumeroSerie(String numeroSerie);
}
