package com.proyecto.proyectoSpringBoot.repository;

import com.proyecto.proyectoSpringBoot.model.entity.UnidadMedida;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UnidadMedidaRepository extends JpaRepository<UnidadMedida, Long> {
    Optional<UnidadMedida> findByCodigo(String codigo);
    List<UnidadMedida> findByActivoTrue();
    boolean existsByCodigo(String codigo);
}
