package com.botica.lisette.controller;

import com.botica.lisette.service.InventarioService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class InventarioController {

    private final InventarioService inventarioService;

    public InventarioController(InventarioService inventarioService) {
        this.inventarioService = inventarioService;
    }

    @GetMapping("/inventario")
    public String inventario(@RequestParam(required = false) String q,
                             @RequestParam(required = false) String estado,
                             Model model) {
        model.addAttribute("filas", inventarioService.listar(q, estado));
        model.addAttribute("q", q);
        model.addAttribute("estado", estado);
        return "inventario";
    }
}