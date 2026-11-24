package com.botica.lisette.controller;

import com.botica.lisette.dto.DashboardResumen;
import com.botica.lisette.service.ConfiguracionService;
import com.botica.lisette.service.DashboardService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Controller
@RequestMapping("/dashboard")
public class DashboardController {

    private static final ZoneId LIMA = ZoneId.of("America/Lima");

    private final DashboardService dashboardService;
    private final ConfiguracionService configuracionService;

    public DashboardController(DashboardService dashboardService, ConfiguracionService configuracionService) {
        this.dashboardService = dashboardService;
        this.configuracionService = configuracionService;
    }

    @GetMapping
    public String dashboard(Model model) {
        model.addAttribute("r", dashboardService.resumen());
        model.addAttribute("diasAviso", configuracionService.diasAvisoVencimiento());
        return "dashboard";
    }

    @GetMapping("/reporte")
    public String reporte(Model model) {
        DashboardResumen r = dashboardService.resumen();
        LocalDateTime ahora = LocalDateTime.now(LIMA);

        BigDecimal maximo = r.montos().stream().max(BigDecimal::compareTo).orElse(BigDecimal.ZERO);
        List<Integer> barras = r.montos().stream()
                .map(m -> maximo.signum() == 0 ? 0
                        : m.multiply(BigDecimal.valueOf(100)).divide(maximo, 0, RoundingMode.HALF_UP).intValue())
                .toList();
        BigDecimal total = r.montos().stream().reduce(BigDecimal.ZERO, BigDecimal::add);

        model.addAttribute("r", r);
        model.addAttribute("barras", barras);
        model.addAttribute("totalPeriodo", total);
        model.addAttribute("diasAviso", configuracionService.diasAvisoVencimiento());
        model.addAttribute("fechaEmision", ahora.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
        model.addAttribute("horaEmision", ahora.format(DateTimeFormatter.ofPattern("HH:mm")));
        model.addAttribute("numero", "RG-" + ahora.format(DateTimeFormatter.ofPattern("yyyyMMdd-HHmm")));
        return "reporte";
    }
}