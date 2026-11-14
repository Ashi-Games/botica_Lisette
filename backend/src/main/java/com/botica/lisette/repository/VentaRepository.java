package com.botica.lisette.repository;

import com.botica.lisette.model.Venta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public interface VentaRepository extends JpaRepository<Venta, Long> {

    @Query("select sum(v.total) from Venta v where v.fechaHora >= :desde and v.fechaHora < :hasta")
    BigDecimal totalEntre(@Param("desde") LocalDateTime desde, @Param("hasta") LocalDateTime hasta);

    @Query("select count(v) from Venta v where v.fechaHora >= :desde and v.fechaHora < :hasta")
    long contarEntre(@Param("desde") LocalDateTime desde, @Param("hasta") LocalDateTime hasta);

    @Query("select year(v.fechaHora), month(v.fechaHora), sum(v.total) from Venta v " +
           "where v.fechaHora >= :desde group by year(v.fechaHora), month(v.fechaHora)")
    List<Object[]> totalesPorMes(@Param("desde") LocalDateTime desde);
}