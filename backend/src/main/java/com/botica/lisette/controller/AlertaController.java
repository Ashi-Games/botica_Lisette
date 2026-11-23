package com.botica.lisette.controller;

import com.botica.lisette.service.AlertaService;
import com.botica.lisette.service.ConfiguracionService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/alertas")
public class AlertaController {

    private final AlertaService alertaService;
    private final ConfiguracionService configuracionService;

    public AlertaController(AlertaService alertaService, ConfiguracionService configuracionService) {
        this.alertaService = alertaService;
        this.configuracionService = configuracionService;
    }

    @GetMapping
    public String alertas(Model model) {
        model.addAttribute("porVencer", alertaService.porVencer());
        model.addAttribute("stockBajo", alertaService.stockBajo());
        model.addAttribute("diasAviso", configuracionService.diasAvisoVencimiento());
        return "alertas";
    }

    @PostMapping("/configuracion")
    public String configurar(@RequestParam(required = false) Integer dias, RedirectAttributes redirect) {
        try {
            configuracionService.cambiarDiasAviso(dias);
            redirect.addFlashAttribute("mensaje", "Listo: se avisará " + dias + " días antes del vencimiento.");
        } catch (IllegalArgumentException e) {
            redirect.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/alertas";
    }
}