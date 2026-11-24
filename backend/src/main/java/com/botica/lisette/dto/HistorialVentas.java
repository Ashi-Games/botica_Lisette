package com.botica.lisette.dto;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

public record HistorialVentas(
        List<VentaResumen> ventas,
        int pagina,
        int totalPaginas,
        BigDecimal total,
        long cantidad) {

    public BigDecimal promedio() {
        if (cantidad == 0) return BigDecimal.ZERO;
        return total.divide(BigDecimal.valueOf(cantidad), 2, RoundingMode.HALF_UP);
    }
}