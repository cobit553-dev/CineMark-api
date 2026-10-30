package com.devsv.cinemark_api.services;

import com.devsv.cinemark_api.dto.auth.LoginRequestDTO;
import com.devsv.cinemark_api.dto.auth.LoginResponseDTO;
import com.devsv.cinemark_api.dto.auth.RegistroClienteDTO;
import com.devsv.cinemark_api.entities.Rol;
import com.devsv.cinemark_api.entities.Usuario;
import com.devsv.cinemark_api.enums.NombreRol;
import com.devsv.cinemark_api.exceptions.ConflictException;
import com.devsv.cinemark_api.exceptions.ResourceNotFoundException;
import com.devsv.cinemark_api.interfaces.IAuthService;
import com.devsv.cinemark_api.repository.RolRepository;
import com.devsv.cinemark_api.repository.UsuarioRepository;
import com.devsv.cinemark_api.security.JwtService;
import com.devsv.cinemark_api.security.UsuarioPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AuthService implements IAuthService {

    private final UsuarioRepository usuarioRepository;
    private final RolRepository rolRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    @Override
    @Transactional
    public LoginResponseDTO registrarCliente(RegistroClienteDTO dto) {
        String email = dto.getEmail().trim().toLowerCase();

        if (usuarioRepository.existsByEmailIgnoreCase(email)) {
            throw new ConflictException("Ya existe un usuario registrado con ese correo");
        }

        Rol rolCliente = rolRepository.findByNombre(NombreRol.CLIENTE)
                .orElseThrow(() -> new ResourceNotFoundException("El rol CLIENTE no está configurado"));

        Usuario usuario = Usuario.builder().rol(rolCliente).nombre(dto.getNombre()).apellido(dto.getApellido())
                .email(email).password(passwordEncoder.encode(dto.getPassword())).telefono(dto.getTelefono())
                .fechaRegistro(LocalDateTime.now()).estado(true).build();

        Usuario guardado = usuarioRepository.save(usuario);
        UsuarioPrincipal principal = new UsuarioPrincipal(guardado);
        String token = jwtService.generarToken(principal);

        return construirRespuesta(guardado, token);
    }

    @Override
    @Transactional(readOnly = true)
    public LoginResponseDTO login(LoginRequestDTO dto) {
        String email = dto.getEmail().trim().toLowerCase();

        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(email, dto.getPassword()));
        UsuarioPrincipal principal = (UsuarioPrincipal) authentication.getPrincipal();
        Usuario usuario = usuarioRepository.findById(principal.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));

        String token = jwtService.generarToken(principal);

        return construirRespuesta(usuario, token);
    }

    private LoginResponseDTO construirRespuesta(Usuario usuario, String token) {
        return new LoginResponseDTO(token, usuario.getId(), usuario.getEmail(), usuario.getNombre(), usuario.getApellido(), usuario.getRol()
                .getNombre().name());
    }
}