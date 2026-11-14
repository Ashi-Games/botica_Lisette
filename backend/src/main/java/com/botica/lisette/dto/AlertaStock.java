package com.botica.lisette.dto;

public record AlertaStock(
    String producto, 
    int stockActual, 
    int stockMinimo) {
        
    }