package com.botica.lisette.controller;

import com.botica.lisette.dto.IngresoForm;
import com.botica.lisette.dto.LineaIngreso;
import com.botica.lisette.model.TipoComprobante;
import com.botica.lisette.repository.ProductoRepository;
import com.botica.lisette.service.IngresoService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
        import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/ingresos")
public class IngresoController {

    private final IngresoService ingresoService;
    private final ProductoRepository productoRepository;

    public IngresoController(IngresoService ingresoService, ProductoRepository productoRepository) {
        this.ingresoService = ingresoService;
        this.productoRepository = productoRepository;
    }

    @GetMapping("/nuevo")
    public String nuevo(Model model) {
        model.addAttribute("form", new IngresoForm());
        cargarListas(model);
        return "ingreso-form";
    }

    @PostMapping
    public String guardar(@ModelAttribute("form") IngresoForm form,
                          Model model,
                          RedirectAttributes redirect) {
        try {
            ingresoService.guardar(form);
            redirect.addFlashAttribute("ok", "Ingreso registrado correctamente.");
            return "redirect:/inventario";
        } catch (IllegalArgumentException e) {
            model.addAttribute("error", e.getMessage());
            cargarListas(model);
            return "ingreso-form";   // vuelve con lo que el usuario ya escribió
        }
    }

    /** Datos que la plantilla necesita siempre. */
    private void cargarListas(Model model) {
        model.addAttribute("tipos", TipoComprobante.values());
        model.addAttribute("categorias", productoRepository.categorias());
        model.addAttribute("productos", productoRepository.findByActivoTrueOrderByNombreAsc());
        model.addAttribute("nuevaLinea", new LineaIngreso());
    }
}