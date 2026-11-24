package com.botica.lisette.controller;

import com.botica.lisette.dto.VentaForm;
import com.botica.lisette.model.Usuario;
import com.botica.lisette.model.Venta;
import com.botica.lisette.repository.UsuarioRepository;
import com.botica.lisette.service.HistorialVentaService;
import com.botica.lisette.service.VentaService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;

@Controller
@RequestMapping("/ventas")
public class VentaController {

    private static final ZoneId LIMA = ZoneId.of("America/Lima");
    private static final List<String> METODOS = List.of("Efectivo", "Yape", "Plin", "Tarjeta");

    private final VentaService ventaService;
    private final HistorialVentaService historialVentaService;
    private final UsuarioRepository usuarioRepository;

    public VentaController(VentaService ventaService, HistorialVentaService historialVentaService,
                           UsuarioRepository usuarioRepository) {
        this.ventaService = ventaService;
        this.historialVentaService = historialVentaService;
        this.usuarioRepository = usuarioRepository;
    }

    @GetMapping
    public String ventas(Model model) {
        model.addAttribute("productos", ventaService.productosParaVenta());
        return "ventas";
    }

    @PostMapping
    public String guardar(@ModelAttribute VentaForm form, Principal principal,
                          RedirectAttributes redirect) {
        try {
            Usuario usuario = usuarioRepository.findByUsername(principal.getName())
                    .orElseThrow(() -> new IllegalStateException("Usuario no encontrado."));
            Venta venta = ventaService.registrar(form, usuario);
            redirect.addFlashAttribute("ok", "Venta registrada. Total: S/ " + venta.getTotal());
        } catch (IllegalArgumentException e) {
            redirect.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/ventas";
    }

    @GetMapping("/historial")
    public String historial(@RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate desde,
                            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate hasta,
                            @RequestParam(required = false) String metodo,
                            @RequestParam(defaultValue = "0") int pagina,
                            Authentication auth,
                            Model model) {
        LocalDate hoy = LocalDate.now(LIMA);
        if (hasta == null) hasta = hoy;
        if (desde == null) desde = hoy.withDayOfMonth(1);           // por defecto: mes actual
        if (desde.isAfter(hasta)) { LocalDate t = desde; desde = hasta; hasta = t; }
        String metodoFiltro = METODOS.contains(metodo) ? metodo : null;

        // La propietaria ve todas las ventas; el vendedor solo las suyas
        boolean admin = auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
        Long usuarioId = admin ? null : usuarioRepository.findByUsername(auth.getName())
                .map(Usuario::getId).orElse(-1L);

        model.addAttribute("h", historialVentaService.buscar(desde, hasta, metodoFiltro, usuarioId, pagina));
        model.addAttribute("desde", desde);
        model.addAttribute("hasta", hasta);
        model.addAttribute("metodo", metodoFiltro);
        model.addAttribute("metodos", METODOS);
        return "ventas-historial";
    }
}