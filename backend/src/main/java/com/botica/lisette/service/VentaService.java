package com.botica.lisette.service;

import com.botica.lisette.dto.LineaVenta;
import com.botica.lisette.dto.ProductoVenta;
import com.botica.lisette.dto.VentaForm;
import com.botica.lisette.model.*;
import com.botica.lisette.repository.LoteRepository;
import com.botica.lisette.repository.ProductoRepository;
import com.botica.lisette.repository.VentaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

@Service
public class VentaService {

    private static final Set<String> METODOS = Set.of("Efectivo", "Yape", "Plin", "Tarjeta");

    private final VentaRepository ventaRepository;
    private final LoteRepository loteRepository;
    private final ProductoRepository productoRepository;

    public VentaService(VentaRepository ventaRepository,
                        LoteRepository loteRepository,
                        ProductoRepository productoRepository) {
        this.ventaRepository = ventaRepository;
        this.loteRepository = loteRepository;
        this.productoRepository = productoRepository;
    }

    /** Productos activos con stock vendible (sin contar lotes vencidos). */
    @Transactional(readOnly = true)
    public List<ProductoVenta> productosParaVenta() {
        LocalDate hoy = LocalDate.now();
        List<ProductoVenta> lista = new ArrayList<>();
        for (Producto p : productoRepository.findByActivoTrueOrderByNombreAsc()) {
            int stock = lotesVendibles(p.getId(), hoy).stream().mapToInt(Lote::getCantidad).sum();
            if (stock > 0) {
                lista.add(new ProductoVenta(p.getId(), p.getNombre(), p.getPrecioVenta(), stock));
            }
        }
        return lista;
    }

    /** Lanza IllegalArgumentException con mensaje legible si algo no cuadra. */
    @Transactional
    public Venta registrar(VentaForm form, Usuario usuario) {

        if (form.getMetodoPago() == null || !METODOS.contains(form.getMetodoPago())) {
            throw new IllegalArgumentException("Elige un método de pago válido.");
        }

        // Juntar líneas repetidas del mismo producto
        Map<Long, Integer> pedido = new LinkedHashMap<>();
        for (LineaVenta l : form.getLineas()) {
            if (l.getProductoId() == null && l.getCantidad() == null) continue;
            if (l.getProductoId() == null || l.getCantidad() == null || l.getCantidad() < 1) {
                throw new IllegalArgumentException("Cada producto debe tener una cantidad mayor a 0.");
            }
            pedido.merge(l.getProductoId(), l.getCantidad(), Integer::sum);
        }
        if (pedido.isEmpty()) {
            throw new IllegalArgumentException("Agrega al menos un producto.");
        }

        LocalDate hoy = LocalDate.now();
        Venta venta = new Venta();
        venta.setFechaHora(LocalDateTime.now());
        venta.setUsuario(usuario);
        venta.setMetodoPago(form.getMetodoPago());

        BigDecimal total = BigDecimal.ZERO;

        for (Map.Entry<Long, Integer> e : pedido.entrySet()) {
            Producto p = productoRepository.findById(e.getKey())
                    .filter(Producto::isActivo)
                    .orElseThrow(() -> new IllegalArgumentException("Un producto de la venta ya no está disponible."));

            List<Lote> lotes = lotesVendibles(p.getId(), hoy);
            int disponible = lotes.stream().mapToInt(Lote::getCantidad).sum();
            int restante = e.getValue();

            if (disponible < restante) {
                throw new IllegalArgumentException("Stock insuficiente de " + p.getNombre()
                        + ": disponible " + disponible + ", pediste " + restante + ".");
            }

            // FEFO: primero el lote que vence antes
            for (Lote lote : lotes) {
                if (restante == 0) break;
                int tomar = Math.min(restante, lote.getCantidad());

                lote.setCantidad(lote.getCantidad() - tomar);
                loteRepository.save(lote);

                BigDecimal subtotal = p.getPrecioVenta()
                        .multiply(BigDecimal.valueOf(tomar))
                        .setScale(2, RoundingMode.HALF_UP);

                DetalleVenta d = new DetalleVenta();
                d.setProducto(p);
                d.setLote(lote);
                d.setCantidad(tomar);
                d.setPrecioUnitario(p.getPrecioVenta());
                d.setSubtotal(subtotal);
                venta.agregarDetalle(d);

                total = total.add(subtotal);
                restante -= tomar;
            }
        }

        venta.setTotal(total);
        return ventaRepository.save(venta);
    }

    /** Lotes con stock que no están vencidos, el que vence primero va primero. */
    private List<Lote> lotesVendibles(Long productoId, LocalDate hoy) {
        return loteRepository
                .findByProductoIdAndCantidadGreaterThanOrderByFechaVencimientoAsc(productoId, 0)
                .stream()
                .filter(l -> !l.getFechaVencimiento().isBefore(hoy))
                .toList();
    }
}