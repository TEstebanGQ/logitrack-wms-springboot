package com.proyecto.proyectoSpringBoot.specification;

import com.proyecto.proyectoSpringBoot.model.entity.Movimiento;
import com.proyecto.proyectoSpringBoot.model.enums.TipoMovimiento;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDateTime;

public class MovimientoSpecification {

    public static Specification<Movimiento> porTipo(TipoMovimiento tipo) {
        return (root, query, cb) ->
                tipo == null ? null : cb.equal(root.get("tipoMovimiento"), tipo);
    }

    public static Specification<Movimiento> tipoEs(TipoMovimiento tipo) {
        return porTipo(tipo);
    }

    public static Specification<Movimiento> entFechas(LocalDateTime inicio, LocalDateTime fin) {
        return (root, query, cb) -> {
            if (inicio == null && fin == null) return null;
            if (inicio == null) return cb.lessThanOrEqualTo(root.get("fecha"), fin);
            if (fin == null) return cb.greaterThanOrEqualTo(root.get("fecha"), inicio);
            return cb.between(root.get("fecha"), inicio, fin);
        };
    }

    public static Specification<Movimiento> fechaEntre(LocalDateTime desde, LocalDateTime hasta) {
        return entFechas(desde, hasta);
    }

    public static Specification<Movimiento> porBodega(Long bodegaId) {
        return (root, query, cb) -> {
            if (bodegaId == null) return null;
            return cb.or(
                    cb.equal(root.get("bodegaOrigen").get("id"), bodegaId),
                    cb.equal(root.get("bodegaDestino").get("id"), bodegaId)
            );
        };
    }

    public static Specification<Movimiento> porProducto(Long productoId) {
        return (root, query, cb) -> {
            if (productoId == null) return null;
            query.distinct(true);
            jakarta.persistence.criteria.Join<Object, Object> detalles = root.join("detalles");
            return cb.equal(detalles.get("producto").get("id"), productoId);
        };
    }

    public static Specification<Movimiento> porUsuario(Long usuarioId) {
        return (root, query, cb) ->
                usuarioId == null ? null : cb.equal(root.get("usuario").get("id"), usuarioId);
    }

    public static Specification<Movimiento> porBodegaOrigen(Long bodegaId) {
        return (root, query, cb) ->
                bodegaId == null ? null : cb.equal(root.get("bodegaOrigen").get("id"), bodegaId);
    }

    public static Specification<Movimiento> porBodegaDestino(Long bodegaId) {
        return (root, query, cb) ->
                bodegaId == null ? null : cb.equal(root.get("bodegaDestino").get("id"), bodegaId);
    }
}
