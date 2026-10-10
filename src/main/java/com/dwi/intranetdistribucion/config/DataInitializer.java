package com.dwi.intranetdistribucion.config;

import com.dwi.intranetdistribucion.model.*;
import com.dwi.intranetdistribucion.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import java.util.*;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {
    private final UsuarioRepository usuarios;
    private final RolRepository roles;
    private final PermisoRepository permisos;
    private final PasswordEncoder encoder;
    @Value("${app.bootstrap.enabled}") private boolean enabled;
    @Value("${app.bootstrap.email}") private String email;
    @Value("${app.bootstrap.password}") private String password;

    @Override
    @Transactional
    public void run(String... args) {
        for (String nombre : List.of("USUARIOS_VER", "USUARIOS_CREAR", "ROLES_GESTIONAR", "INVENTARIO_VER", "INVENTARIO_GESTIONAR")) {
            if (!permisos.existsByNombre(nombre)) permisos.save(Permiso.builder()
                .nombre(nombre).descripcion(nombre.replace('_', ' ')).modulo(nombre.startsWith("INVENTARIO") ? "INVENTARIO" : "SEGURIDAD").build());
        }
        crearRol("ADMIN", permisos.findAll());
        crearRol("ALMACENERO", List.of(permisos.findByNombre("INVENTARIO_VER").orElseThrow(), permisos.findByNombre("INVENTARIO_GESTIONAR").orElseThrow()));
        crearRol("AUDITOR", List.of(permisos.findByNombre("INVENTARIO_VER").orElseThrow()));
        if (enabled && !usuarios.existsByEmail(email)) {
            if (password.length() < 12) throw new IllegalArgumentException("ADMIN_PASSWORD debe tener al menos 12 caracteres");
            usuarios.save(Usuario.builder().nombre("Administrador").apellido("Académico").email(email)
                .password(encoder.encode(password)).rol(roles.findByNombre("ADMIN").orElseThrow()).build());
        }
    }
    private void crearRol(String nombre, List<Permiso> lista) {
        if (!roles.existsByNombre(nombre)) roles.save(Rol.builder().nombre(nombre).descripcion("Rol base " + nombre).permisos(new HashSet<>(lista)).build());
    }
}
