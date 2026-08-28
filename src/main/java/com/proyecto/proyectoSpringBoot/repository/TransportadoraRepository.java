package com.proyecto.proyectoSpringBoot.repository;

import com.proyecto.proyectoSpringBoot.model.entity.Transportadora;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TransportadoraRepository extends JpaRepository<Transportadora, Long> {
    Optional<Transportadora> findByRucNit(String rucNit);
    List<Transportadora> findByActivoTrue();
    boolean existsByRucNit(String rucNit);
}
