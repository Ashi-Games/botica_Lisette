package com.botica.lisette.controller;

import com.botica.lisette.service.AlertaService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/alertas")
public class AlertaController {

    private final AlertaService alertaService;

    public AlertaController(AlertaService alertaService) {
        this.alertaService = alertaService;
    }

    @GetMapping
    public String alertas(Model model) {
        model.addAttribute("porVencer", alertaService.porVencer());
        model.addAttribute("stockBajo", alertaService.stockBajo());
        return "alertas";
    }
}