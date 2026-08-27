package com.proyecto.proyectoSpringBoot.repository;

import com.proyecto.proyectoSpringBoot.model.entity.InventarioBodega;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface InventarioBodegaRepository extends JpaRepository<InventarioBodega, Long> {
    List<InventarioBodega> findByBodegaId(Long bodegaId);
    List<InventarioBodega> findByProductoId(Long productoId);
    Optional<InventarioBodega> findByBodegaIdAndProductoId(Long bodegaId, Long productoId);

    @Query("SELECT ib.bodega.id, ib.bodega.nombre, SUM(ib.stockActual) " +
           "FROM InventarioBodega ib GROUP BY ib.bodega.id, ib.bodega.nombre")
    List<Object[]> findStockTotalPorBodega();
}
