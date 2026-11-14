package com.botica.lisette.dto;

import java.time.LocalDate;

public record AlertaVencimiento(
    String producto, 
    LocalDate fechaVencimiento, 
    long dias, 
    int stock) {
        
    }