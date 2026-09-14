package com.devsv.cinemark_api.services;

import com.devsv.cinemark_api.dto.CambiarPasswordDTO;
import com.devsv.cinemark_api.dto.PerfilDTO;
import com.devsv.cinemark_api.dto.PerfilUpdateDTO;
import com.devsv.cinemark_api.entities.Usuario;
import com.devsv.cinemark_api.exceptions.BadRequestException;
import com.devsv.cinemark_api.exceptions.ConflictException;
import com.devsv.cinemark_api.exceptions.ResourceNotFoundException;
import com.devsv.cinemark_api.interfaces.IPerfilService;
import com.devsv.cinemark_api.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PerfilService implements IPerfilService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional(readOnly = true)
    public PerfilDTO obtenerPerfil(String emailUsuario) {
        return convertirDTO(obtenerUsuario(emailUsuario));
    }

    @Override
    @Transactional
    public PerfilDTO actualizarPerfil(PerfilUpdateDTO dto, String emailUsuario) {
        validarPerfil(dto);

        Usuario usuario = obtenerUsuario(emailUsuario);

        usuario.setNombre(dto.getNombre().trim());
        usuario.setApellido(dto.getApellido().trim());
        usuario.setTelefono(dto.getTelefono() != null ? dto.getTelefono().trim() : null);

        return convertirDTO(usuarioRepository.save(usuario));
    }

    @Override
    @Transactional
    public void cambiarPassword(CambiarPasswordDTO dto, String emailUsuario) {
        validarCambioPassword(dto);

        Usuario usuario = obtenerUsuario(emailUsuario);

        if (!passwordEncoder.matches(dto.getPasswordActual(), usuario.getPassword())) {
            throw new ConflictException("La contraseña actual es incorrecta");
        }

        if (passwordEncoder.matches(dto.getPasswordNueva(), usuario.getPassword())) {
            throw new ConflictException("La nueva contraseña debe ser diferente a la actual");
        }

        usuario.setPassword(passwordEncoder.encode(dto.getPasswordNueva()));
        usuarioRepository.save(usuario);
    }

    private Usuario obtenerUsuario(String emailUsuario) {
        return usuarioRepository.findByEmailIgnoreCase(emailUsuario)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario autenticado no encontrado"));
    }

    private void validarPerfil(PerfilUpdateDTO dto) {
        if (dto == null) {
            throw new BadRequestException("Los datos del perfil son obligatorios");
        }

        if (dto.getNombre() == null || dto.getNombre().isBlank()) {
            throw new BadRequestException("El nombre es obligatorio");
        }

        if (dto.getApellido() == null || dto.getApellido().isBlank()) {
            throw new BadRequestException("El apellido es obligatorio");
        }
    }

    private void validarCambioPassword(CambiarPasswordDTO dto) {
        if (dto == null) {
            throw new BadRequestException("Los datos de contraseña son obligatorios");
        }

        if (dto.getPasswordActual() == null || dto.getPasswordActual().isBlank()) {
            throw new BadRequestException("La contraseña actual es obligatoria");
        }

        if (dto.getPasswordNueva() == null || dto.getPasswordNueva().isBlank()) {
            throw new BadRequestException("La nueva contraseña es obligatoria");
        }

        if (dto.getPasswordNueva().length() < 8) {
            throw new BadRequestException("La nueva contraseña debe tener al menos 8 caracteres");
        }
    }

    private PerfilDTO convertirDTO(Usuario usuario) {
        return PerfilDTO.builder().id(usuario.getId()).nombre(usuario.getNombre()).apellido(usuario.getApellido())
                .email(usuario.getEmail()).telefono(usuario.getTelefono())
                .rol(usuario.getRol() != null && usuario.getRol().getNombre() != null ?
                        usuario.getRol().getNombre().name() : null).build();
    }
}