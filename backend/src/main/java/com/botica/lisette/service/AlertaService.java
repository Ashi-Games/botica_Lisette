package com.botica.lisette.service;

import com.botica.lisette.dto.AlertaStock;
import com.botica.lisette.dto.AlertaVencimiento;
import com.botica.lisette.model.Lote;
import com.botica.lisette.model.Producto;
import com.botica.lisette.repository.LoteRepository;
import com.botica.lisette.repository.ProductoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Comparator;
import java.util.List;

@Service
public class AlertaService {

    public static final int DIAS_AVISO = 30;

    private final LoteRepository loteRepository;
    private final ProductoRepository productoRepository;

    public AlertaService(LoteRepository loteRepository, ProductoRepository productoRepository) {
        this.loteRepository = loteRepository;
        this.productoRepository = productoRepository;
    }

    /** Lotes con stock que vencen en los próximos 30 días (o ya vencieron). */
    @Transactional(readOnly = true)
    public List<AlertaVencimiento> porVencer() {
        LocalDate hoy = LocalDate.now();
        LocalDate limite = hoy.plusDays(DIAS_AVISO);

        List<Lote> lotes = loteRepository
                .findByCantidadGreaterThanAndFechaVencimientoLessThanEqualOrderByFechaVencimientoAsc(0, limite);

        return lotes.stream()
                .map(l -> new AlertaVencimiento(
                        l.getProducto().getNombre(),
                        l.getFechaVencimiento(),
                        ChronoUnit.DAYS.between(hoy, l.getFechaVencimiento()),
                        l.getCantidad()))
                .toList();
    }

    /** Productos activos con stock total por debajo de su mínimo. */
    @Transactional(readOnly = true)
    public List<AlertaStock> stockBajo() {
        return productoRepository.findByActivoTrueOrderByNombreAsc().stream()
                .map(p -> new AlertaStock(p.getNombre(), stockDe(p), p.getStockMinimo()))
                .filter(a -> a.stockActual() < a.stockMinimo())
                .sorted(Comparator.comparingInt(AlertaStock::stockActual))
                .toList();
    }

    public int totalAlertas() {
        return porVencer().size() + stockBajo().size();
    }

    private int stockDe(Producto p) {
        Long total = loteRepository.stockTotal(p.getId());
        return total == null ? 0 : total.intValue();
    }
}