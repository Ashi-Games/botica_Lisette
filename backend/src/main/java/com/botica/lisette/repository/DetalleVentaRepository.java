package com.botica.lisette.repository;

import com.botica.lisette.model.DetalleVenta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface DetalleVentaRepository extends JpaRepository<DetalleVenta, Long> {

    /** [productoId, unidades vendidas] desde una fecha. */
    @Query("select d.producto.id, sum(d.cantidad) from DetalleVenta d " +
           "where d.venta.fechaHora >= :desde group by d.producto.id")
    List<Object[]> vendidoPorProducto(@Param("desde") LocalDateTime desde);
}