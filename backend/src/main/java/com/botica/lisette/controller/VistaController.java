
package com.botica.lisette.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;
import java.util.Map;

@Controller
public class VistaController {

    // PANTALLA DE INICIO DE SESION
    @GetMapping("/")
    public String inicio() {
        return "redirect:/login";
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    // DASHBOARD
    @GetMapping("/dashboard")
    public String dashboard(Model model) {

        model.addAttribute("ventasDia", "486.50");
        model.addAttribute("ventasMes", "9,870");
        model.addAttribute("cantidadVentas", 312);
        model.addAttribute("productosAlerta", 8);

        model.addAttribute("productosVendidos", List.of(
                Map.of("nombre", "Paracetamol 500 mg", "cantidad", 148),
                Map.of("nombre", "Ibuprofeno 400 mg", "cantidad", 121),
                Map.of("nombre", "Suero oral", "cantidad", 96),
                Map.of("nombre", "Loratadina 10 mg", "cantidad", 84)
        ));

        model.addAttribute("productosMenosVendidos", List.of(
                Map.of("nombre", "Mascarilla KN95", "cantidad", 3),
                Map.of("nombre", "Termómetro digital", "cantidad", 4),
                Map.of("nombre", "Diclofenaco gel 30 g", "cantidad", 6),
                Map.of("nombre", "Vitamina C 1 g", "cantidad", 7)
        ));

        return "dashboard";
    }
}
