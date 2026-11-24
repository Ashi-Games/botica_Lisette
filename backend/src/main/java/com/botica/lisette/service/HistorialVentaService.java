package com.botica.lisette.service;

import com.botica.lisette.dto.HistorialVentas;
import com.botica.lisette.dto.VentaLinea;
import com.botica.lisette.dto.VentaResumen;
import com.botica.lisette.model.DetalleVenta;
import com.botica.lisette.model.Venta;
import com.botica.lisette.repository.VentaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class HistorialVentaService {

    public static final int POR_PAGINA = 20;

    private final VentaRepository ventaRepository;

    public HistorialVentaService(VentaRepository ventaRepository) {
        this.ventaRepository = ventaRepository;
    }

    /** usuarioId = null → todas las ventas; con valor → solo las de ese usuario. */
    @Transactional(readOnly = true)
    public HistorialVentas buscar(LocalDate desde, LocalDate hasta, String metodo, Long usuarioId, int pagina) {
        LocalDateTime ini = desde.atStartOfDay();
        LocalDateTime fin = hasta.plusDays(1).atStartOfDay();

        Page<Venta> page = ventaRepository.buscar(ini, fin, metodo, usuarioId,
                PageRequest.of(Math.max(pagina, 0), POR_PAGINA));
        List<VentaResumen> filas = page.getContent().stream().map(this::resumen).toList();

        Object[] t = ventaRepository.totales(ini, fin, metodo, usuarioId).get(0);
        BigDecimal total = (t[0] == null) ? BigDecimal.ZERO : new BigDecimal(t[0].toString());
        long cantidad = (t[1] == null) ? 0 : ((Number) t[1]).longValue();

        return new HistorialVentas(filas, page.getNumber(), page.getTotalPages(), total, cantidad);
    }

    private VentaResumen resumen(Venta v) {
        // Un producto puede salir de varios lotes: se juntan en una sola línea
        Map<String, VentaLinea> agrupado = new LinkedHashMap<>();
        int unidades = 0;
        for (DetalleVenta d : v.getDetalles()) {
            String nombre = d.getProducto().getNombre();
            VentaLinea prev = agrupado.get(nombre);
            agrupado.put(nombre, (prev == null)
                    ? new VentaLinea(nombre, d.getCantidad(), d.getPrecioUnitario(), d.getSubtotal())
                    : new VentaLinea(nombre, prev.cantidad() + d.getCantidad(),
                                     prev.precioUnitario(), prev.subtotal().add(d.getSubtotal())));
            unidades += d.getCantidad();
        }
        String vendedor = (v.getUsuario().getNombreCompleto() != null)
                ? v.getUsuario().getNombreCompleto() : v.getUsuario().getUsername();

        return new VentaResumen(v.getId(), v.getFechaHora(), vendedor, v.getMetodoPago(),
                v.getTotal(), unidades, new ArrayList<>(agrupado.values()));
    }
}