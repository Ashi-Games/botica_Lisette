
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


    // ALERTAS
    @GetMapping("/alertas")
    public String alertas(Model model) {

        model.addAttribute("proximosVencer", List.of(
                Map.of("nombre", "Jarabe para la tos 120 ml", "fecha", "25/10/2026", "dias", 9, "stock", 3),
                Map.of("nombre", "Loratadina 10 mg", "fecha", "30/10/2026", "dias", 14, "stock", 42),
                Map.of("nombre", "Amoxicilina 500 mg", "fecha", "02/11/2026", "dias", 17, "stock", 18),
                Map.of("nombre", "Vitamina C 1 g", "fecha", "09/11/2026", "dias", 24, "stock", 33),
                Map.of("nombre", "Suero oral sobre", "fecha", "12/11/2026", "dias", 27, "stock", 55)
        ));

        model.addAttribute("reposicion", List.of(
                Map.of("nombre", "Paracetamol 500 mg", "stock", 5, "minimo", 20),
                Map.of("nombre", "Amoxicilina 500 mg", "stock", 18, "minimo", 25),
                Map.of("nombre", "Omeprazol 20 mg", "stock", 9, "minimo", 15),
                Map.of("nombre", "Diclofenaco gel 30 g", "stock", 7, "minimo", 10),
                Map.of("nombre", "Jarabe para la tos 120 ml", "stock", 3, "minimo", 8)
        ));

        return "alertas";
    }

    // REGISTRAR VENTAS
    @GetMapping("/ventas")
    public String ventas(Model model) {

        model.addAttribute("productos", List.of(
                Map.of("nombre", "Paracetamol 500 mg", "precio", 0.50, "stock", 5),
                Map.of("nombre", "Ibuprofeno 400 mg", "precio", 0.80, "stock", 64),
                Map.of("nombre", "Suero oral sobre", "precio", 2.50, "stock", 55),
                Map.of("nombre", "Loratadina 10 mg", "precio", 1.00, "stock", 42),
                Map.of("nombre", "Amoxicilina 500 mg", "precio", 1.50, "stock", 18)
        ));

        return "ventas";
    }
}
