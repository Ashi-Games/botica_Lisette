package com.botica.lisette.service;

import com.botica.lisette.model.Configuracion;
import com.botica.lisette.repository.ConfiguracionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ConfiguracionService {

    public static final int DIAS_POR_DEFECTO = 30;
    public static final int DIAS_MIN = 1;
    public static final int DIAS_MAX = 180;

    private final ConfiguracionRepository repo;

    public ConfiguracionService(ConfiguracionRepository repo) {
        this.repo = repo;
    }

    /** Días de anticipación con que se avisa un vencimiento. */
    @Transactional(readOnly = true)
    public int diasAvisoVencimiento() {
        return repo.findById(1L)
                .map(Configuracion::getDiasAvisoVencimiento)
                .orElse(DIAS_POR_DEFECTO);
    }

    @Transactional
    public void cambiarDiasAviso(Integer dias) {
        if (dias == null || dias < DIAS_MIN || dias > DIAS_MAX) {
            throw new IllegalArgumentException("Elige un número de días entre " + DIAS_MIN + " y " + DIAS_MAX + ".");
        }
        Configuracion c = repo.findById(1L).orElseGet(Configuracion::new);
        c.setDiasAvisoVencimiento(dias);
        repo.save(c);
    }
}