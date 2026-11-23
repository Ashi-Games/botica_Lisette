package com.botica.lisette.service;

import com.botica.lisette.dto.ProductoEditForm;
import com.botica.lisette.model.Lote;
import com.botica.lisette.model.Producto;
import com.botica.lisette.repository.LoteRepository;
import com.botica.lisette.repository.ProductoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ProductoService {

    private final ProductoRepository productoRepository;
    private final LoteRepository loteRepository;

    public ProductoService(ProductoRepository productoRepository, LoteRepository loteRepository) {
        this.productoRepository = productoRepository;
        this.loteRepository = loteRepository;
    }

    /** Llena el formulario con los datos actuales del producto y sus lotes con stock. */
    @Transactional(readOnly = true)
    public ProductoEditForm formulario(Long id) {
        Producto p = buscar(id);
        ProductoEditForm f = new ProductoEditForm();
        f.setNombre(p.getNombre());
        f.setCategoria(p.getCategoria());
        f.setPrecioVenta(p.getPrecioVenta());
        f.setStockMinimo(p.getStockMinimo());

        for (Lote l : loteRepository.findByProductoIdAndCantidadGreaterThanOrderByFechaVencimientoAsc(id, 0)) {
            ProductoEditForm.LoteEdit le = new ProductoEditForm.LoteEdit();
            le.setId(l.getId());
            le.setComprobante(l.getIngreso().getComprobanteTexto());
            le.setCodigoLote(l.getCodigoLote());
            le.setCantidad(l.getCantidad());
            le.setFechaVencimiento(l.getFechaVencimiento());
            f.getLotes().add(le);
        }
        return f;
    }

    /** Guarda los cambios. Si algo no cuadra, lanza IllegalArgumentException y no se guarda nada. */
    @Transactional
    public void guardar(Long id, ProductoEditForm f) {
        Producto p = buscar(id);

        if (vacio(f.getNombre()) || vacio(f.getCategoria())) {
            throw new IllegalArgumentException("Escribe el nombre y la categoría del producto.");
        }
        String nombre = f.getNombre().trim();
        if (productoRepository.existsByNombreIgnoreCaseAndIdNot(nombre, id)) {
            throw new IllegalArgumentException("Ya existe otro producto llamado \"" + nombre + "\".");
        }
        if (f.getPrecioVenta() == null || f.getPrecioVenta().signum() <= 0) {
            throw new IllegalArgumentException("El precio de venta debe ser mayor a 0.");
        }
        if (f.getStockMinimo() == null || f.getStockMinimo() < 0) {
            throw new IllegalArgumentException("El stock mínimo no puede ser negativo.");
        }

        for (ProductoEditForm.LoteEdit le : f.getLotes()) {
            Lote l = (le.getId() == null) ? null : loteRepository.findById(le.getId())
                    .filter(x -> x.getProducto().getId().equals(id))
                    .orElse(null);
            if (l == null) {
                throw new IllegalArgumentException("Uno de los lotes no pertenece a este producto.");
            }
            String ref = "Lote " + (vacio(l.getCodigoLote()) ? "sin código" : l.getCodigoLote()) + ": ";
            if (le.getCantidad() == null || le.getCantidad() < 0) {
                throw new IllegalArgumentException(ref + "la cantidad no puede ser negativa.");
            }
            if (le.getFechaVencimiento() == null) {
                throw new IllegalArgumentException(ref + "indica la fecha de vencimiento.");
            }
            l.setCantidad(le.getCantidad());
            l.setFechaVencimiento(le.getFechaVencimiento());
        }

        p.setNombre(nombre);
        p.setCategoria(f.getCategoria().trim());
        p.setPrecioVenta(f.getPrecioVenta());
        p.setStockMinimo(f.getStockMinimo());
    }

    @Transactional(readOnly = true)
    public List<String> categorias() {
        return productoRepository.categorias();
    }

    private Producto buscar(Long id) {
        return productoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("El producto no existe."));
    }

    private boolean vacio(String s) {
        return s == null || s.isBlank();
    }
}