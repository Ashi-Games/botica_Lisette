package com.botica.lisette.controller;

import com.botica.lisette.ia.IAException;
import com.botica.lisette.service.PrediccionService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/predicciones")
public class PrediccionController {

    private final PrediccionService prediccionService;

    public PrediccionController(PrediccionService prediccionService) {
        this.prediccionService = prediccionService;
    }

    @GetMapping
    public String ver(@RequestParam(required = false) String actualizar, Model model) {
        try {
            model.addAttribute("ia", prediccionService.obtener(actualizar != null));
        } catch (IAException e) {
            model.addAttribute("iaError", e.getMessage());
        }
        return "predicciones";
    }
}