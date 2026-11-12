package com.botica.lisette.model;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "ingreso")
public class Ingreso {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 120)
    private String proveedor;

    @Column(name = "tipo_comprobante", nullable = false, length = 10)
    private String tipoComprobante;

    @Column(name = "codigo_comprobante", nullable = false, length = 30)
    private String codigoComprobante;

    @Column(name = "fecha_comprobante", nullable = false)
    private LocalDate fechaComprobante;

    @Column(name = "fecha_registro", nullable = false)
    private LocalDateTime fechaRegistro;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id")
    private Usuario usuario;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getProveedor() { return proveedor; }
    public void setProveedor(String proveedor) { this.proveedor = proveedor; }
    public String getTipoComprobante() { return tipoComprobante; }
    public void setTipoComprobante(String tipoComprobante) { this.tipoComprobante = tipoComprobante; }
    public String getCodigoComprobante() { return codigoComprobante; }
    public void setCodigoComprobante(String codigoComprobante) { this.codigoComprobante = codigoComprobante; }
    public LocalDate getFechaComprobante() { return fechaComprobante; }
    public void setFechaComprobante(LocalDate fechaComprobante) { this.fechaComprobante = fechaComprobante; }
    public LocalDateTime getFechaRegistro() { return fechaRegistro; }
    public void setFechaRegistro(LocalDateTime fechaRegistro) { this.fechaRegistro = fechaRegistro; }
    public Usuario getUsuario() { return usuario; }
    public void setUsuario(Usuario usuario) { this.usuario = usuario; }
}