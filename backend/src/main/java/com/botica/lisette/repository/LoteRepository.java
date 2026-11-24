package com.botica.lisette.repository;

import com.botica.lisette.model.Lote;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.time.LocalDate;
import java.util.List;

public interface LoteRepository extends JpaRepository<Lote, Long> {

    /** Stock total de un producto = suma de todos sus lotes. */
    @Query("select coalesce(sum(l.cantidad), 0) from Lote l where l.producto.id = :productoId")
    Long stockTotal(@Param("productoId") Long productoId);

    /** Fecha de vencimiento más próxima entre los lotes con stock. */
    @Query("select min(l.fechaVencimiento) from Lote l where l.producto.id = :productoId and l.cantidad > 0")
    LocalDate proximoVencimiento(@Param("productoId") Long productoId);

    /** Lotes con stock, el que vence primero va primero (FEFO). Se usa al vender y al editar. */
    List<Lote> findByProductoIdAndCantidadGreaterThanOrderByFechaVencimientoAsc(Long productoId, int cantidad);

    /** Lotes con stock que vencen antes de cierta fecha. Se usa en las alertas. */
    List<Lote> findByCantidadGreaterThanAndFechaVencimientoLessThanEqualOrderByFechaVencimientoAsc(int cantidad, LocalDate limite);

    /** [productoId, stock, vencimiento más próximo, cantidad de lotes] de los lotes con stock. */
    @Query("select l.producto.id, sum(l.cantidad), min(l.fechaVencimiento), count(l) " +
           "from Lote l where l.cantidad > 0 group by l.producto.id")
    List<Object[]> resumenPorProducto();

    /** [productoId, unidades] de lotes con stock que NO están vencidos (stock realmente vendible). */
    @Query("select l.producto.id, sum(l.cantidad) from Lote l " +
           "where l.cantidad > 0 and l.fechaVencimiento >= :hoy group by l.producto.id")
    List<Object[]> stockVendible(@Param("hoy") LocalDate hoy);
}