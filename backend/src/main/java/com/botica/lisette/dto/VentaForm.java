package com.botica.lisette.dto;

import java.util.ArrayList;
import java.util.List;

public class VentaForm {
    private String metodoPago;
    private List<LineaVenta> lineas = new ArrayList<>();

    public String getMetodoPago() { return metodoPago; }
    public void setMetodoPago(String metodoPago) { this.metodoPago = metodoPago; }
    public List<LineaVenta> getLineas() { return lineas; }
    public void setLineas(List<LineaVenta> lineas) { this.lineas = lineas; }
}