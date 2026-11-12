package com.botica.lisette.repository;

import org.springframework.data.jpa.repository.Query;
import com.botica.lisette.model.Producto;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ProductoRepository extends JpaRepository<Producto, Long> {
    List<Producto> findByActivoTrueOrderByNombreAsc();
    List<Producto> findByActivoTrueAndNombreContainingIgnoreCaseOrderByNombreAsc(String nombre);

    boolean existsByNombreIgnoreCase(String nombre);

    @Query("select distinct p.categoria from Producto p order by p.categoria")
    List<String> categorias();
}
