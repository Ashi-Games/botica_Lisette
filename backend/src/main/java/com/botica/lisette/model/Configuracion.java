package com.botica.lisette.model;

import jakarta.persistence.*;

@Entity
@Table(name = "configuracion")
public class Configuracion {

    @Id
    private Long id = 1L;

    @Column(name = "dias_aviso_vencimiento", nullable = false)
    private int diasAvisoVencimiento = 30;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public int getDiasAvisoVencimiento() { return diasAvisoVencimiento; }
    public void setDiasAvisoVencimiento(int dias) { this.diasAvisoVencimiento = dias; }
}