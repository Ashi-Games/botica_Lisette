package com.botica.lisette.repository;

import com.botica.lisette.model.Ingreso;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface IngresoRepository extends JpaRepository<Ingreso, Long> {

    /** Para avisar "este comprobante ya fue registrado" antes de guardar. */
    boolean existsByProveedorIgnoreCaseAndTipoComprobanteAndCodigoComprobante(
            String proveedor, String tipoComprobante, String codigoComprobante);

    List<Ingreso> findAllByOrderByFechaComprobanteDesc();

}