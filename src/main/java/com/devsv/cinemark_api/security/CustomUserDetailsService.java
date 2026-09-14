package com.devsv.cinemark_api.security;

import com.devsv.cinemark_api.entities.Usuario;
import com.devsv.cinemark_api.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UsuarioRepository usuarioRepository;

    // DANIEL - SEGURIDAD: carga usuario por email para autenticación (login), lanza excepción si no existe
    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String email) {

        Usuario usuario = usuarioRepository.findByEmailIgnoreCase(email)
                .orElseThrow(() -> new UsernameNotFoundException("Correo o contraseña incorrectos"));

        return new UsuarioPrincipal(usuario);
    }
}