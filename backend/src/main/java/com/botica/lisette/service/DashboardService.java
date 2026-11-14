package com.botica.lisette.service;

import com.botica.lisette.dto.*;
import com.botica.lisette.model.Producto;
import com.botica.lisette.repository.DetalleVentaRepository;
import com.botica.lisette.repository.ProductoRepository;
import com.botica.lisette.repository.VentaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.*;
import java.util.stream.Stream;

@Service
public class DashboardService {

    private static final String[] MESES =
            {"Ene", "Feb", "Mar", "Abr", "May", "Jun", "Jul", "Ago", "Sep", "Oct", "Nov", "Dic"};
    private static final String VERDE = "#087F75";
    private static final String ROJO = "#E45D5D";
    private static final String GRIS = "#718096";

    private final VentaRepository ventaRepository;
    private final DetalleVentaRepository detalleVentaRepository;
    private final ProductoRepository productoRepository;
    private final AlertaService alertaService;

    public DashboardService(VentaRepository ventaRepository,
                            DetalleVentaRepository detalleVentaRepository,
                            ProductoRepository productoRepository,
                            AlertaService alertaService) {
        this.ventaRepository = ventaRepository;
        this.detalleVentaRepository = detalleVentaRepository;
        this.productoRepository = productoRepository;
        this.alertaService = alertaService;
    }

    @Transactional(readOnly = true)
    public DashboardResumen resumen() {
        LocalDate hoy = LocalDate.now();
        LocalDateTime inicioHoy = hoy.atStartOfDay();
        LocalDateTime inicioManana = inicioHoy.plusDays(1);
        LocalDateTime inicioAyer = inicioHoy.minusDays(1);

        LocalDate primeroMes = hoy.withDayOfMonth(1);
        LocalDateTime inicioMes = primeroMes.atStartOfDay();
        LocalDateTime inicioMesAnt = primeroMes.minusMonths(1).atStartOfDay();
        // mismo tramo del mes anterior (los mismos días transcurridos)
        LocalDateTime finMesAnt = inicioMesAnt.plusDays(hoy.getDayOfMonth());
        if (finMesAnt.isAfter(inicioMes)) finMesAnt = inicioMes;

        Indicador ventasDia = indicador(monto(inicioHoy, inicioManana), monto(inicioAyer, inicioHoy), "ayer", true);
        Indicador ventasMes = indicador(monto(inicioMes, inicioManana), monto(inicioMesAnt, finMesAnt), "mes anterior", true);
        Indicador cantidad = indicador(
                BigDecimal.valueOf(ventaRepository.contarEntre(inicioMes, inicioManana)),
                BigDecimal.valueOf(ventaRepository.contarEntre(inicioMesAnt, finMesAnt)),
                "mes anterior", false);

        // Gráfico: últimos 6 meses
        Map<Integer, BigDecimal> porMes = new HashMap<>();
        for (Object[] f : ventaRepository.totalesPorMes(primeroMes.minusMonths(5).atStartOfDay())) {
            porMes.put(((Number) f[0]).intValue() * 100 + ((Number) f[1]).intValue(), (BigDecimal) f[2]);
        }
        List<String> meses = new ArrayList<>();
        List<BigDecimal> montos = new ArrayList<>();
        for (int i = 5; i >= 0; i--) {
            YearMonth ym = YearMonth.from(hoy).minusMonths(i);
            meses.add(MESES[ym.getMonthValue() - 1]);
            montos.add(porMes.getOrDefault(ym.getYear() * 100 + ym.getMonthValue(), BigDecimal.ZERO));
        }

        // Más y menos vendidos (últimos 30 días)
        Map<Long, Long> vendido = new HashMap<>();
        for (Object[] f : detalleVentaRepository.vendidoPorProducto(inicioHoy.minusDays(30))) {
            vendido.put((Long) f[0], ((Number) f[1]).longValue());
        }
        List<VendidoProducto> todos = productoRepository.findByActivoTrueOrderByNombreAsc().stream()
                .map((Producto p) -> new VendidoProducto(p.getNombre(), vendido.getOrDefault(p.getId(), 0L)))
                .toList();
        List<VendidoProducto> mas = todos.stream()
                .filter(v -> v.cantidad() > 0)
                .sorted(Comparator.comparingLong(VendidoProducto::cantidad).reversed())
                .limit(4).toList();
        List<VendidoProducto> menos = todos.stream()
                .sorted(Comparator.comparingLong(VendidoProducto::cantidad))
                .limit(4).toList();

        // Alertas
        List<AlertaVencimiento> porVencer = alertaService.porVencer();
        List<AlertaStock> stockBajo = alertaService.stockBajo();
        int productosAlerta = (int) Stream.concat(
                porVencer.stream().map(AlertaVencimiento::producto),
                stockBajo.stream().map(AlertaStock::producto)).distinct().count();

        return new DashboardResumen(ventasDia, ventasMes, cantidad, productosAlerta,
                meses, montos, mas, menos, porVencer, stockBajo);
    }

    private BigDecimal monto(LocalDateTime desde, LocalDateTime hasta) {
        BigDecimal t = ventaRepository.totalEntre(desde, hasta);
        return t == null ? BigDecimal.ZERO : t;
    }

    private Indicador indicador(BigDecimal actual, BigDecimal anterior, String contra, boolean dinero) {
        String valor = dinero
                ? String.format(Locale.US, "%,.2f", actual)
                : String.valueOf(actual.longValue());

        if (anterior.signum() == 0) {
            return new Indicador(valor, "Sin datos de " + contra, GRIS);
        }
        int pct = actual.subtract(anterior).multiply(BigDecimal.valueOf(100))
                .divide(anterior, 0, RoundingMode.HALF_UP).intValue();
        String texto = (pct >= 0 ? "↑ " : "↓ ") + Math.abs(pct) + "% vs. " + contra;
        return new Indicador(valor, texto, pct >= 0 ? VERDE : ROJO);
    }
}