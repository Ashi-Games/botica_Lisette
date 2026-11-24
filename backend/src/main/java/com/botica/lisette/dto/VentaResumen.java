package com.botica.lisette.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record VentaResumen(
        Long id,
        LocalDateTime fechaHora,
        String vendedor,
        String metodoPago,
        BigDecimal total,
        int unidades,
        List<VentaLinea> lineas) {
}