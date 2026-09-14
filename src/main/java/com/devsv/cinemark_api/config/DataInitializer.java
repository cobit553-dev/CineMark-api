package com.devsv.cinemark_api.config;

import com.devsv.cinemark_api.entities.Rol;
import com.devsv.cinemark_api.entities.Usuario;
import com.devsv.cinemark_api.enums.NombreRol;
import com.devsv.cinemark_api.repository.RolRepository;
import com.devsv.cinemark_api.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;

@Configuration
public class DataInitializer {

    @Value("${app.admin.email}")
    private String adminEmail;

    @Value("${app.admin.password}")
    private String adminPassword;

    @Bean
    CommandLineRunner inicializarDatos(RolRepository rolRepository, UsuarioRepository usuarioRepository,
            PasswordEncoder passwordEncoder) {
        return args -> {

            Rol admin = rolRepository.findByNombre(NombreRol.ADMIN)
                    .orElseGet(() -> rolRepository.save(Rol.builder().nombre(NombreRol.ADMIN)
                            .descripcion("Administrador del sistema").estado(true).build()));

            rolRepository.findByNombre(NombreRol.CLIENTE)
                    .orElseGet(() -> rolRepository.save(Rol.builder().nombre(NombreRol.CLIENTE)
                            .descripcion("Cliente del cine").estado(true).build()));

            if (!usuarioRepository.existsByEmailIgnoreCase(adminEmail)) {

                Usuario usuarioAdmin = Usuario.builder().rol(admin).nombre("Administrador").apellido("CineMark")
                        .email(adminEmail.trim().toLowerCase()).password(passwordEncoder.encode(adminPassword))
                        .telefono(null).fechaRegistro(LocalDateTime.now()).estado(true).build();

                usuarioRepository.save(usuarioAdmin);
            }
        };
    }
}