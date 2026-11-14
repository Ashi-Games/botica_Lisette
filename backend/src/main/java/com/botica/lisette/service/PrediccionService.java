package com.botica.lisette.service;

import com.botica.lisette.ia.IA;
import com.botica.lisette.model.Producto;
import com.botica.lisette.repository.DetalleVentaRepository;
import com.botica.lisette.repository.LoteRepository;
import com.botica.lisette.repository.ProductoRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Date;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.util.*;

/** Arma los datos (ventas por día + stock) y se los pasa al módulo de Python. Guarda el resultado 10 min. */
@Service
public class PrediccionService {

    private static final Duration VIGENCIA = Duration.ofMinutes(10);

    private final ProductoRepository productoRepository;
    private final LoteRepository loteRepository;
    private final DetalleVentaRepository detalleVentaRepository;
    private final IAClientService iaClient;
    private final int diasHistorial;

    private IA.Respuesta cache;
    private Instant cacheHora = Instant.EPOCH;

    public PrediccionService(ProductoRepository productoRepository,
                             LoteRepository loteRepository,
                             DetalleVentaRepository detalleVentaRepository,
                             IAClientService iaClient,
                             @Value("${app.ia.dias-historial:365}") int diasHistorial) {
        this.productoRepository = productoRepository;
        this.loteRepository = loteRepository;
        this.detalleVentaRepository = detalleVentaRepository;
        this.iaClient = iaClient;
        this.diasHistorial = diasHistorial;
    }

    @Transactional(readOnly = true)
    public synchronized IA.Respuesta obtener(boolean forzar) {
        if (!forzar && cache != null && Duration.between(cacheHora, Instant.now()).compareTo(VIGENCIA) < 0) {
            return cache;
        }
        LocalDate hoy = LocalDate.now();

        Map<Long, Long> stock = new HashMap<>();
        for (Object[] f : loteRepository.stockVendible(hoy)) {
            stock.put((Long) f[0], ((Number) f[1]).longValue());
        }
        Map<Long, List<IA.Dia>> ventas = new HashMap<>();
        for (Object[] f : detalleVentaRepository.unidadesDiarias(hoy.minusDays(diasHistorial).atStartOfDay())) {
            LocalDate fecha = f[1] instanceof Date d ? d.toLocalDate() : LocalDate.parse(f[1].toString());
            ventas.computeIfAbsent(((Number) f[0]).longValue(), k -> new ArrayList<>())
                  .add(new IA.Dia(fecha, ((Number) f[2]).longValue()));
        }

        List<IA.ProductoEntrada> productos = new ArrayList<>();
        for (Producto p : productoRepository.findByActivoTrueOrderByNombreAsc()) {
            productos.add(new IA.ProductoEntrada(p.getId(), p.getNombre(),
                    stock.getOrDefault(p.getId(), 0L).intValue(), p.getStockMinimo(),
                    ventas.getOrDefault(p.getId(), List.of())));
        }

        cache = iaClient.analizar(new IA.Solicitud(hoy, productos));
        cacheHora = Instant.now();
        return cache;
    }
}