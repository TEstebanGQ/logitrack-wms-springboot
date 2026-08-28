package com.proyecto.proyectoSpringBoot.repository;

import com.proyecto.proyectoSpringBoot.model.entity.Lote;
import com.proyecto.proyectoSpringBoot.model.enums.EstadoLote;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface LoteRepository extends JpaRepository<Lote, Long> {
    List<Lote> findByProductoId(Long productoId);
    List<Lote> findByBodegaId(Long bodegaId);
    List<Lote> findByEstado(EstadoLote estado);
    List<Lote> findByProductoIdAndBodegaIdOrderByFechaVencimientoAsc(Long productoId, Long bodegaId);
    List<Lote> findByFechaVencimientoBetween(LocalDate inicio, LocalDate fin);
    List<Lote> findByFechaVencimientoBeforeAndEstadoNot(LocalDate fecha, EstadoLote estado);
    Optional<Lote> findByCodigoLoteAndProductoIdAndBodegaId(String codigoLote, Long productoId, Long bodegaId);
    boolean existsByCodigoLoteAndProductoIdAndBodegaId(String codigoLote, Long productoId, Long bodegaId);
}
