package com.botica.lisette.model;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "lote")
public class Lote {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "producto_id", nullable = false)
    private Producto producto;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "ingreso_id", nullable = false)
    private Ingreso ingreso;

    @Column(name = "codigo_lote", length = 40)
    private String codigoLote;

    @Column(nullable = false)
    private int cantidad;

    @Column(name = "fecha_vencimiento", nullable = false)
    private LocalDate fechaVencimiento;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Producto getProducto() { return producto; }
    public void setProducto(Producto producto) { this.producto = producto; }
    public String getCodigoLote() { return codigoLote; }
    public void setCodigoLote(String codigoLote) { this.codigoLote = codigoLote; }
    public int getCantidad() { return cantidad; }
    public void setCantidad(int cantidad) { this.cantidad = cantidad; }
    public LocalDate getFechaVencimiento() { return fechaVencimiento; }
    public void setFechaVencimiento(LocalDate fechaVencimiento) { this.fechaVencimiento = fechaVencimiento; }
    public Ingreso getIngreso() { return ingreso; }
    public void setIngreso(Ingreso ingreso) { this.ingreso = ingreso; }
}