error id: file:///C:/Users/MASTER/Documents/botica_Lisette/backend/src/main/java/com/botica/lisette/service/AlertaService.java:java/time/LocalDate#
file:///C:/Users/MASTER/Documents/botica_Lisette/backend/src/main/java/com/botica/lisette/service/AlertaService.java
empty definition using pc, found symbol in pc: java/time/LocalDate#
empty definition using semanticdb
empty definition using fallback
non-local guesses:

offset: 469
uri: file:///C:/Users/MASTER/Documents/botica_Lisette/backend/src/main/java/com/botica/lisette/service/AlertaService.java
text:
```scala
package com.botica.lisette.service;

import com.botica.lisette.dto.AlertaStock;
import com.botica.lisette.dto.AlertaVencimiento;
import com.botica.lisette.model.Lote;
import com.botica.lisette.model.Producto;
import com.botica.lisette.repository.LoteRepository;
import com.botica.lisette.repository.ProductoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate@@;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.Comparator;
import java.util.List;

@Service
public class AlertaService {

    private static final ZoneId LIMA = ZoneId.of("America/Lima");

    private final LoteRepository loteRepository;
    private final ProductoRepository productoRepository;
    private final ConfiguracionService configuracionService;

    public AlertaService(LoteRepository loteRepository, ProductoRepository productoRepository,
                         ConfiguracionService configuracionService) {
        this.loteRepository = loteRepository;
        this.productoRepository = productoRepository;
        this.configuracionService = configuracionService;
    }

    /** Un aviso por lote (cada boleta o factura tiene su propia fecha de vencimiento). */
    @Transactional(readOnly = true)
    public List<AlertaVencimiento> porVencer() {
        LocalDate hoy = LocalDate.now(LIMA);
        LocalDate limite = hoy.plusDays(configuracionService.diasAvisoVencimiento());

        List<Lote> lotes = loteRepository
                .findByCantidadGreaterThanAndFechaVencimientoLessThanEqualOrderByFechaVencimientoAsc(0, limite);

        return lotes.stream()
                .map(l -> new AlertaVencimiento(
                        l.getProducto().getId(),
                        l.getProducto().getNombre(),
                        l.getIngreso().getComprobanteTexto(),
                        l.getIngreso().getProveedor(),
                        l.getCodigoLote(),
                        l.getFechaVencimiento(),
                        ChronoUnit.DAYS.between(hoy, l.getFechaVencimiento()),
                        l.getCantidad()))
                .toList();
    }

    /** Productos activos con stock total por debajo de su mínimo. */
    @Transactional(readOnly = true)
    public List<AlertaStock> stockBajo() {
        return productoRepository.findByActivoTrueOrderByNombreAsc().stream()
                .map(p -> new AlertaStock(p.getNombre(), stockDe(p), p.getStockMinimo()))
                .filter(a -> a.stockActual() < a.stockMinimo())
                .sorted(Comparator.comparingInt(AlertaStock::stockActual))
                .toList();
    }

    public int totalAlertas() {
        return porVencer().size() + stockBajo().size();
    }

    private int stockDe(Producto p) {
        Long total = loteRepository.stockTotal(p.getId());
        return total == null ? 0 : total.intValue();
    }
}
```


#### Short summary: 

empty definition using pc, found symbol in pc: java/time/LocalDate#