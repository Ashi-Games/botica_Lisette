package com.botica.lisette.controller;

import com.botica.lisette.dto.ProductoEditForm;
import com.botica.lisette.service.InventarioService;
import com.botica.lisette.service.ProductoService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class InventarioController {

    private final InventarioService inventarioService;
    private final ProductoService productoService;

    public InventarioController(InventarioService inventarioService, ProductoService productoService) {
        this.inventarioService = inventarioService;
        this.productoService = productoService;
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

    @GetMapping("/inventario/{id}/editar")
    public String editar(@PathVariable Long id, Model model, RedirectAttributes redirect) {
        try {
            model.addAttribute("form", productoService.formulario(id));
        } catch (IllegalArgumentException e) {
            redirect.addFlashAttribute("error", e.getMessage());
            return "redirect:/inventario";
        }
        cargarDatos(id, model);
        return "producto-editar";
    }

    @PostMapping("/inventario/{id}/editar")
    public String guardar(@PathVariable Long id,
                          @ModelAttribute("form") ProductoEditForm form,
                          Model model,
                          RedirectAttributes redirect) {
        try {
            productoService.guardar(id, form);
            redirect.addFlashAttribute("mensaje", "Producto actualizado correctamente.");
            return "redirect:/inventario";
        } catch (IllegalArgumentException e) {
            model.addAttribute("error", e.getMessage());
            cargarDatos(id, model);
            return "producto-editar";   // vuelve con lo que ya escribió
        }
    }

    private void cargarDatos(Long id, Model model) {
        model.addAttribute("productoId", id);
        model.addAttribute("categorias", productoService.categorias());
    }
}