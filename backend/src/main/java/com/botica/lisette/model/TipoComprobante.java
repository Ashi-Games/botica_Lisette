package com.botica.lisette.model;

public enum TipoComprobante {
    BOLETA("Boleta"),
    FACTURA("Factura");

    private final String etiqueta;

    TipoComprobante(String etiqueta) { this.etiqueta = etiqueta; }

    public String getEtiqueta() { return etiqueta; }
}