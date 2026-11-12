package com.botica.lisette.dto;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class IngresoForm {
    private String proveedor;
    private String tipoComprobante;   // String: la plantilla lo compara con t.name()
    private String codigoComprobante;
    private LocalDate fechaComprobante;
    private List<LineaIngreso> lineas = new ArrayList<>();

    public IngresoForm() {
        lineas.add(new LineaIngreso());   // el formulario arranca con 1 línea
    }

    public String getProveedor() { return proveedor; }
    public void setProveedor(String proveedor) { this.proveedor = proveedor; }
    public String getTipoComprobante() { return tipoComprobante; }
    public void setTipoComprobante(String t) { this.tipoComprobante = t; }
    public String getCodigoComprobante() { return codigoComprobante; }
    public void setCodigoComprobante(String c) { this.codigoComprobante = c; }
    public LocalDate getFechaComprobante() { return fechaComprobante; }
    public void setFechaComprobante(LocalDate f) { this.fechaComprobante = f; }
    public List<LineaIngreso> getLineas() { return lineas; }
    public void setLineas(List<LineaIngreso> lineas) { this.lineas = lineas; }
}