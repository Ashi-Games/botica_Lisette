package com.botica.lisette.service;

import com.botica.lisette.dto.IngresoForm;
import com.botica.lisette.dto.LineaIngreso;
import com.botica.lisette.model.*;
        import com.botica.lisette.repository.IngresoRepository;
import com.botica.lisette.repository.LoteRepository;
import com.botica.lisette.repository.ProductoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class IngresoService {

    private final IngresoRepository ingresoRepository;
    private final LoteRepository loteRepository;
    private final ProductoRepository productoRepository;

    public IngresoService(IngresoRepository ingresoRepository,
                          LoteRepository loteRepository,
                          ProductoRepository productoRepository) {
        this.ingresoRepository = ingresoRepository;
        this.loteRepository = loteRepository;
        this.productoRepository = productoRepository;
    }

    /** Lanza IllegalArgumentException con un mensaje legible si algo no cuadra. */
    @Transactional
    public void guardar(IngresoForm form) {

        // 1. Comprobante
        if (vacio(form.getProveedor()) || vacio(form.getCodigoComprobante())
                || vacio(form.getTipoComprobante()) || form.getFechaComprobante() == null) {
            throw new IllegalArgumentException("Completa todos los datos del comprobante.");
        }
        TipoComprobante tipo;
        try {
            tipo = TipoComprobante.valueOf(form.getTipoComprobante());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Elige un tipo de comprobante válido.");
        }
        String proveedor = form.getProveedor().trim();
        String codigo = form.getCodigoComprobante().trim();

        if (ingresoRepository.existsByProveedorIgnoreCaseAndTipoComprobanteAndCodigoComprobante(
                proveedor, tipo.name(), codigo)) {
            throw new IllegalArgumentException("Ese comprobante ya fue registrado para este proveedor.");
        }

        // 2. Líneas (se ignoran las vacías que deja el botón "Quitar")
        List<LineaIngreso> lineas = form.getLineas().stream()
                .filter(l -> !lineaVacia(l))
                .toList();
        if (lineas.isEmpty()) {
            throw new IllegalArgumentException("Agrega al menos un producto.");
        }

        // 3. Guardar ingreso
        Ingreso ingreso = new Ingreso();
        ingreso.setProveedor(proveedor);
        ingreso.setTipoComprobante(tipo.name());
        ingreso.setCodigoComprobante(codigo);
        ingreso.setFechaComprobante(form.getFechaComprobante());
        ingreso.setFechaRegistro(LocalDateTime.now());
        ingresoRepository.save(ingreso);

        // 4. Guardar lotes (y productos nuevos si hace falta)
        int n = 1;
        for (LineaIngreso l : lineas) {
            String fila = "Producto " + n++ + ": ";

            if (l.getCantidad() == null || l.getCantidad() < 1) {
                throw new IllegalArgumentException(fila + "la cantidad debe ser mayor a 0.");
            }
            if (l.getFechaVencimiento() == null || l.getFechaVencimiento().isBefore(LocalDate.now())) {
                throw new IllegalArgumentException(fila + "la fecha de vencimiento no puede estar vencida.");
            }

            Producto producto = (l.getProductoId() != null)
                    ? productoRepository.findById(l.getProductoId())
                    .orElseThrow(() -> new IllegalArgumentException(fila + "el producto no existe."))
                    : crearProducto(l, fila);

            Lote lote = new Lote();
            lote.setProducto(producto);
            lote.setIngreso(ingreso);
            lote.setCodigoLote(vacio(l.getCodigoLote()) ? null : l.getCodigoLote().trim());
            lote.setCantidad(l.getCantidad());
            lote.setFechaVencimiento(l.getFechaVencimiento());
            loteRepository.save(lote);
        }
    }

    private Producto crearProducto(LineaIngreso l, String fila) {
        if (vacio(l.getNombre()) || vacio(l.getCategoria())) {
            throw new IllegalArgumentException(fila + "escribe el nombre y la categoría del producto nuevo.");
        }
        if (l.getPrecioVenta() == null || l.getPrecioVenta().signum() <= 0) {
            throw new IllegalArgumentException(fila + "el precio de venta debe ser mayor a 0.");
        }
        String nombre = l.getNombre().trim();
        if (productoRepository.existsByNombreIgnoreCase(nombre)) {
            throw new IllegalArgumentException(fila + "ya existe un producto llamado \"" + nombre
                    + "\". Elígelo de la lista.");
        }
        Producto p = new Producto();
        p.setNombre(nombre);
        p.setCategoria(l.getCategoria().trim());
        p.setPrecioVenta(l.getPrecioVenta());
        p.setStockMinimo(l.getStockMinimo() == null ? 0 : l.getStockMinimo());
        p.setActivo(true);
        return productoRepository.save(p);
    }

    private boolean lineaVacia(LineaIngreso l) {
        return l.getProductoId() == null && l.getCantidad() == null
                && vacio(l.getNombre()) && l.getFechaVencimiento() == null;
    }

    private boolean vacio(String s) {
        return s == null || s.isBlank();
    }
}