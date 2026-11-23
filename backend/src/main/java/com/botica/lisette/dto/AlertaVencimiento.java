package com.botica.lisette.dto;

import java.time.LocalDate;

public record AlertaVencimiento(
        Long productoId,
        String producto,
        String comprobante,
        String proveedor,
        String codigoLote,
        LocalDate fechaVencimiento,
        long dias,
        int stock) {
}