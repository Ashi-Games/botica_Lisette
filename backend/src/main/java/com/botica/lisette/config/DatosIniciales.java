package com.botica.lisette.config;

import com.botica.lisette.model.Usuario;
import com.botica.lisette.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DatosIniciales implements CommandLineRunner {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder encoder;

    @Value("${app.admin-password:admin123}")
    private String adminPassword;

    @Value("${app.vendedor-password:vendedor123}")
    private String vendedorPassword;

    public DatosIniciales(UsuarioRepository usuarioRepository, PasswordEncoder encoder) {
        this.usuarioRepository = usuarioRepository;
        this.encoder = encoder;
    }

    @Override
    public void run(String... args) {
        asegurar("admin", "Propietaria", "ADMIN", adminPassword);
        asegurar("vendedor", "Empleado", "VENDEDOR", vendedorPassword);
    }

    private void asegurar(String username, String nombre, String rol, String clave) {
        Usuario u = usuarioRepository.findByUsername(username).orElseGet(() -> {
            Usuario nuevo = new Usuario();
            nuevo.setUsername(username);
            nuevo.setNombreCompleto(nombre);
            nuevo.setRol(rol);
            nuevo.setActivo(true);
            return nuevo;
        });
        // Si la clave aún no es BCrypt (ej. la 'temporal'), se convierte
        if (u.getPassword() == null || !u.getPassword().startsWith("$2")) {
            u.setPassword(encoder.encode(clave));
        }
        usuarioRepository.save(u);
    }
}