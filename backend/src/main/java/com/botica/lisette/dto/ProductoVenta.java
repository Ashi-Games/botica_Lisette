package com.botica.lisette.dto;

import java.math.BigDecimal;

public record ProductoVenta(
    Long id, 
    String nombre, 
    BigDecimal precio, 
    int stock) {
        
    }