package com.botica.lisette.controller;

import com.botica.lisette.dto.VentaForm;
import com.botica.lisette.model.Usuario;
import com.botica.lisette.model.Venta;
import com.botica.lisette.repository.UsuarioRepository;
import com.botica.lisette.service.VentaService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/ventas")
public class VentaController {

    private final VentaService ventaService;
    private final UsuarioRepository usuarioRepository;

    public VentaController(VentaService ventaService, UsuarioRepository usuarioRepository) {
        this.ventaService = ventaService;
        this.usuarioRepository = usuarioRepository;
    }

    @GetMapping
    public String ventas(Model model) {
        model.addAttribute("productos", ventaService.productosParaVenta());
        return "ventas";
    }

    @PostMapping
    public String guardar(@ModelAttribute VentaForm form, RedirectAttributes redirect) {
        try {
            Venta venta = ventaService.registrar(form, usuarioActual());
            redirect.addFlashAttribute("ok", "Venta registrada. Total: S/ " + venta.getTotal());
        } catch (IllegalArgumentException e) {
            redirect.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/ventas";
    }

    // TEMPORAL: en el bloque de roles se cambia por el usuario que inició sesión
    private Usuario usuarioActual() {
        return usuarioRepository.findByUsername("admin")
                .orElseThrow(() -> new IllegalStateException("Falta el usuario 'admin' en la base."));
    }
}