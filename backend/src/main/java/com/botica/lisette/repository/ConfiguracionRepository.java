package com.botica.lisette.repository;

import com.botica.lisette.model.Configuracion;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ConfiguracionRepository extends JpaRepository<Configuracion, Long> {
}