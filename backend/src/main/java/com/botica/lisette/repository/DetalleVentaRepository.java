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
        /** [productoId, día, unidades vendidas ese día] desde una fecha. Alimenta la predicción. */
    @Query(value = "select dv.producto_id, date(v.fecha_hora), sum(dv.cantidad) " +
                   "from detalle_venta dv join venta v on v.id = dv.venta_id " +
                   "where v.fecha_hora >= :desde group by dv.producto_id, date(v.fecha_hora)",
           nativeQuery = true)
    List<Object[]> unidadesDiarias(@Param("desde") LocalDateTime desde);
}