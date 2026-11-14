package com.botica.lisette.config;

import com.botica.lisette.model.Usuario;
import com.botica.lisette.repository.UsuarioRepository;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

@ControllerAdvice
public class SesionAdvice {

    private final UsuarioRepository usuarioRepository;

    public SesionAdvice(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    @ModelAttribute
    public void datosSesion(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || auth instanceof AnonymousAuthenticationToken || !auth.isAuthenticated()) {
            return;
        }
        boolean admin = auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
        String nombre = usuarioRepository.findByUsername(auth.getName())
                .map(Usuario::getNombreCompleto)
                .orElse(auth.getName());

        model.addAttribute("esAdmin", admin);
        model.addAttribute("usuarioNombre", nombre);
        model.addAttribute("usuarioRol", admin ? "Administrador" : "Vendedor");
    }
}