package com.botica.lisette.dto;

import org.springframework.format.annotation.DateTimeFormat;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class ProductoEditForm {

    private String nombre;
    private String categoria;
    private BigDecimal precioVenta;
    private Integer stockMinimo;
    private List<LoteEdit> lotes = new ArrayList<>();

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public String getCategoria() { return categoria; }
    public void setCategoria(String categoria) { this.categoria = categoria; }
    public BigDecimal getPrecioVenta() { return precioVenta; }
    public void setPrecioVenta(BigDecimal precioVenta) { this.precioVenta = precioVenta; }
    public Integer getStockMinimo() { return stockMinimo; }
    public void setStockMinimo(Integer stockMinimo) { this.stockMinimo = stockMinimo; }
    public List<LoteEdit> getLotes() { return lotes; }
    public void setLotes(List<LoteEdit> lotes) { this.lotes = lotes; }

    /** Un lote = productos que llegaron con una boleta o factura. */
    public static class LoteEdit {
        private Long id;
        private String comprobante;   // solo para mostrar
        private String codigoLote;    // solo para mostrar
        private Integer cantidad;
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
        private LocalDate fechaVencimiento;

        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }
        public String getComprobante() { return comprobante; }
        public void setComprobante(String comprobante) { this.comprobante = comprobante; }
        public String getCodigoLote() { return codigoLote; }
        public void setCodigoLote(String codigoLote) { this.codigoLote = codigoLote; }
        public Integer getCantidad() { return cantidad; }
        public void setCantidad(Integer cantidad) { this.cantidad = cantidad; }
        public LocalDate getFechaVencimiento() { return fechaVencimiento; }
        public void setFechaVencimiento(LocalDate f) { this.fechaVencimiento = f; }
    }
}