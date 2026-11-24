package com.botica.lisette.dto;

import java.math.BigDecimal;

/** Un producto dentro de una venta (ya sumado si salió de varios lotes). */
public record VentaLinea(String producto, int cantidad, BigDecimal precioUnitario, BigDecimal subtotal) {
}