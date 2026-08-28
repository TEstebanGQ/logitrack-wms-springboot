package com.proyecto.proyectoSpringBoot.repository;

import com.proyecto.proyectoSpringBoot.model.entity.Movimiento;
import com.proyecto.proyectoSpringBoot.model.enums.TipoMovimiento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface MovimientoRepository extends JpaRepository<Movimiento, Long>,
        JpaSpecificationExecutor<Movimiento> {

    List<Movimiento> findByUsuarioId(Long usuarioId);
    List<Movimiento> findByTipoMovimiento(TipoMovimiento tipo);
    List<Movimiento> findByFechaBetween(LocalDateTime inicio, LocalDateTime fin);

    @Query("SELECT m FROM Movimiento m WHERE m.bodegaOrigen.id = :bodegaId OR m.bodegaDestino.id = :bodegaId")
    List<Movimiento> findByBodegaId(@Param("bodegaId") Long bodegaId);

    List<Movimiento> findByProveedorId(Long proveedorId);
    List<Movimiento> findByClienteId(Long clienteId);

    @Query("SELECT md.producto.id, SUM(md.cantidad) as totalMovido " +
           "FROM MovimientoDetalle md GROUP BY md.producto.id ORDER BY totalMovido DESC")
    List<Object[]> findProductosMasMovidos();
}
