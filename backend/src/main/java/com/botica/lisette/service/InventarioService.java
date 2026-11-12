package com.botica.lisette.service;

import com.botica.lisette.dto.InventarioFila;
import com.botica.lisette.model.Producto;
import com.botica.lisette.repository.LoteRepository;
import com.botica.lisette.repository.ProductoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Transactional(readOnly = true)
public class InventarioService {

    private static final ZoneId LIMA = ZoneId.of("America/Lima");
    public static final int DIAS_POR_VENCER = 30;

    private final ProductoRepository productoRepository;
    private final LoteRepository loteRepository;

    public InventarioService(ProductoRepository productoRepository, LoteRepository loteRepository) {
        this.productoRepository = productoRepository;
        this.loteRepository = loteRepository;
    }

    /** estado: vacío = todos, o NORMAL / BAJO / POR_VENCER / VENCIDO. */
    public List<InventarioFila> listar(String q, String estado) {
        String texto = (q == null) ? "" : q.trim();
        List<Producto> productos =
                productoRepository.findByActivoTrueAndNombreContainingIgnoreCaseOrderByNombreAsc(texto);

        Map<Long, Object[]> resumen = new HashMap<>();
        for (Object[] r : loteRepository.resumenPorProducto()) {
            resumen.put((Long) r[0], r);
        }

        LocalDate hoy = LocalDate.now(LIMA);
        List<InventarioFila> filas = new ArrayList<>();
        for (Producto p : productos) {
            Object[] r = resumen.get(p.getId());
            long stock = (r == null) ? 0 : ((Number) r[1]).longValue();
            LocalDate vence = (r == null) ? null : (LocalDate) r[2];
            String est = calcularEstado(stock, p.getStockMinimo(), vence, hoy);

            if (estado == null || estado.isBlank() || estado.equals(est)) {
                filas.add(new InventarioFila(p.getId(), p.getNombre(), p.getCategoria(),
                        stock, p.getStockMinimo(), vence, est));
            }
        }
        return filas;
    }

    private String calcularEstado(long stock, int stockMinimo, LocalDate vence, LocalDate hoy) {
        if (vence != null && vence.isBefore(hoy)) return "VENCIDO";
        if (vence != null && !vence.isAfter(hoy.plusDays(DIAS_POR_VENCER))) return "POR_VENCER";
        if (stock <= stockMinimo) return "BAJO";
        return "NORMAL";
    }
}