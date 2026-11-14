package com.botica.lisette.ia;

import java.time.LocalDate;
import java.util.List;

/** Mensajes que se intercambian con el módulo de Python (JSON en camelCase). */
public final class IA {
    private IA() {}

    public record Dia(LocalDate fecha, long unidades) {}

    public record ProductoEntrada(Long id, String nombre, int stock, int stockMinimo, List<Dia> ventas) {}

    public record Solicitud(LocalDate hoy, List<ProductoEntrada> productos) {}

    public record Fila(Long productoId, String producto, int unidadesSemana, int unidadesMes,
                       String tendencia, String confianza, String metodo,
                       int stock, Integer diasDeStock, int comprarSugerido) {}

    public record Respuesta(String generadoEn, int totalSemana, int totalMes, List<Fila> productos) {}
}