package com.botica.lisette.dto;

import java.math.BigDecimal;
import java.util.List;

public record DashboardResumen(
        Indicador ventasDia,
        Indicador ventasMes,
        Indicador cantidadVentas,
        int productosAlerta,
        List<String> meses,
        List<BigDecimal> montos,
        List<VendidoProducto> masVendidos,
        List<VendidoProducto> menosVendidos,
        List<AlertaVencimiento> porVencer,
        List<AlertaStock> stockBajo) {}