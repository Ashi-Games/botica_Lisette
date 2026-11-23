error id: file:///C:/Users/MASTER/Documents/botica_Lisette/backend/src/main/java/com/botica/lisette/dto/InventarioFila.java:java/time/LocalDate#
file:///C:/Users/MASTER/Documents/botica_Lisette/backend/src/main/java/com/botica/lisette/dto/InventarioFila.java
empty definition using pc, found symbol in pc: java/time/LocalDate#
empty definition using semanticdb
empty definition using fallback
non-local guesses:

offset: 444
uri: file:///C:/Users/MASTER/Documents/botica_Lisette/backend/src/main/java/com/botica/lisette/dto/InventarioFila.java
text:
```scala
package com.botica.lisette.dto;

import java.time.LocalDate;

public class InventarioFila {
    private final Long id;
    private final String nombre;
    private final String categoria;
    private final long stock;
    private final int stockMinimo;
    private final LocalDate vence;
    private final long lotes;
    private final String estado; // NORMAL, BAJO, POR_VENCER, VENCIDO

    public InventarioFila(Long id, String@@ nombre, String categoria, long stock,
                          int stockMinimo, LocalDate vence, long lotes, String estado) {
        this.id = id; this.nombre = nombre; this.categoria = categoria; this.stock = stock;
        this.stockMinimo = stockMinimo; this.vence = vence; this.lotes = lotes; this.estado = estado;
    }

    public Long getId() { return id; }
    public String getNombre() { return nombre; }
    public String getCategoria() { return categoria; }
    public long getStock() { return stock; }
    public int getStockMinimo() { return stockMinimo; }
    public LocalDate getVence() { return vence; }
    public long getLotes() { return lotes; }
    public String getEstado() { return estado; }

    public String getEstadoTexto() {
        return switch (estado) {
            case "VENCIDO" -> "Vencido";
            case "POR_VENCER" -> "Por vencer";
            case "BAJO" -> "Stock bajo";
            default -> "Normal";
        };
    }

    /** Clase CSS de la etiqueta: ok (verde), aviso (ámbar), peligro (rojo). */
    public String getEstadoClase() {
        return switch (estado) {
            case "VENCIDO", "POR_VENCER" -> "peligro";
            case "BAJO" -> "aviso";
            default -> "ok";
        };
    }
}
```


#### Short summary: 

empty definition using pc, found symbol in pc: java/time/LocalDate#