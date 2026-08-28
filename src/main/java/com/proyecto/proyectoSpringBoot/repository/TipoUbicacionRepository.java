package com.proyecto.proyectoSpringBoot.repository;

import com.proyecto.proyectoSpringBoot.model.entity.TipoUbicacion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TipoUbicacionRepository extends JpaRepository<TipoUbicacion, Long> {
    Optional<TipoUbicacion> findByCodigo(String codigo);
    List<TipoUbicacion> findByActivoTrue();
    boolean existsByCodigo(String codigo);
}
