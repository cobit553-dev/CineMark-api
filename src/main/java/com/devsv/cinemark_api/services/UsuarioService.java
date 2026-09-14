package com.devsv.cinemark_api.services;

import com.devsv.cinemark_api.dto.PerfilClienteUpdateDTO;
import com.devsv.cinemark_api.dto.UsuarioDTO;
import com.devsv.cinemark_api.entities.Rol;
import com.devsv.cinemark_api.entities.Usuario;
import com.devsv.cinemark_api.exceptions.BadRequestException;
import com.devsv.cinemark_api.exceptions.ConflictException;
import com.devsv.cinemark_api.exceptions.ResourceNotFoundException;
import com.devsv.cinemark_api.interfaces.IUsuarioService;
import com.devsv.cinemark_api.mappers.UsuarioMapper;
import com.devsv.cinemark_api.repository.RolRepository;
import com.devsv.cinemark_api.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UsuarioService implements IUsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final RolRepository rolRepository;
    private final UsuarioMapper usuarioMapper;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional(readOnly = true)
    public List<UsuarioDTO> findAll() {
        return usuarioMapper.toDtoList(usuarioRepository.findAll());
    }

    @Override
    @Transactional(readOnly = true)
    public UsuarioDTO findById(Long id) {
        return usuarioMapper.toDTO(obtenerUsuario(id));
    }

    @Override
    @Transactional(readOnly = true)
    public UsuarioDTO findPerfil(String email) {
        return usuarioMapper.toDTO(obtenerUsuarioPorEmail(email));
    }

    @Override
    @Transactional
    public UsuarioDTO create(UsuarioDTO dto) {
        validarDatosBasicos(dto);
        validarPasswordObligatoria(dto.getPassword());

        String email = normalizarEmail(dto.getEmail());
        validarEmailDuplicado(email);

        Rol rol = obtenerRol(dto.getRolId());
        Usuario usuario = usuarioMapper.toEntity(dto);

        usuario.setId(null);
        usuario.setRol(rol);
        usuario.setNombre(dto.getNombre().trim());
        usuario.setApellido(dto.getApellido().trim());
        usuario.setEmail(email);
        usuario.setTelefono(limpiarTelefono(dto.getTelefono()));
        usuario.setPassword(passwordEncoder.encode(dto.getPassword()));
        usuario.setFechaRegistro(LocalDateTime.now());

        if (usuario.getEstado() == null) {
            usuario.setEstado(true);
        }

        return usuarioMapper.toDTO(usuarioRepository.save(usuario));
    }

    @Override
    @Transactional
    public UsuarioDTO update(Long id, UsuarioDTO dto) {
        validarDatosBasicos(dto);

        Usuario usuario = obtenerUsuario(id);
        Rol rol = obtenerRol(dto.getRolId());
        String email = normalizarEmail(dto.getEmail());

        validarEmailDuplicadoEnEdicion(id, email);

        usuario.setRol(rol);
        usuario.setNombre(dto.getNombre().trim());
        usuario.setApellido(dto.getApellido().trim());
        usuario.setEmail(email);
        usuario.setTelefono(limpiarTelefono(dto.getTelefono()));

        if (dto.getPassword() != null && !dto.getPassword().isBlank()) {
            validarLongitudPassword(dto.getPassword());
            usuario.setPassword(passwordEncoder.encode(dto.getPassword()));
        }

        if (dto.getEstado() != null) {
            usuario.setEstado(dto.getEstado());
        }

        return usuarioMapper.toDTO(usuarioRepository.save(usuario));
    }

    @Override
    @Transactional
    public UsuarioDTO updatePerfil(String email, PerfilClienteUpdateDTO dto) {
        validarDatosPerfil(dto);

        Usuario usuario = obtenerUsuarioPorEmail(email);

        usuario.setNombre(dto.getNombre().trim());
        usuario.setApellido(dto.getApellido().trim());
        usuario.setTelefono(limpiarTelefono(dto.getTelefono()));

        if (dto.getPassword() != null && !dto.getPassword().isBlank()) {
            usuario.setPassword(passwordEncoder.encode(dto.getPassword()));
        }

        return usuarioMapper.toDTO(usuarioRepository.save(usuario));
    }

    @Override
    @Transactional
    public void delete(Long id) {
        usuarioRepository.delete(obtenerUsuario(id));
    }

    private Usuario obtenerUsuario(Long id) {
        return usuarioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado con id: " + id));
    }

    private Usuario obtenerUsuarioPorEmail(String email) {
        if (email == null || email.isBlank()) {
            throw new BadRequestException("No se pudo identificar al usuario autenticado");
        }

        return usuarioRepository.findByEmailIgnoreCase(email.trim())
                .orElseThrow(() -> new ResourceNotFoundException("Usuario autenticado no encontrado"));
    }

    private Rol obtenerRol(Long rolId) {
        return rolRepository.findById(rolId)
                .orElseThrow(() -> new ResourceNotFoundException("Rol no encontrado con id: " + rolId));
    }

    private void validarDatosBasicos(UsuarioDTO dto) {
        if (dto == null) {
            throw new BadRequestException("Los datos del usuario son obligatorios");
        }

        if (dto.getRolId() == null) {
            throw new BadRequestException("El rol es obligatorio");
        }

        if (dto.getNombre() == null || dto.getNombre().isBlank()) {
            throw new BadRequestException("El nombre es obligatorio");
        }

        if (dto.getApellido() == null || dto.getApellido().isBlank()) {
            throw new BadRequestException("El apellido es obligatorio");
        }

        if (dto.getEmail() == null || dto.getEmail().isBlank()) {
            throw new BadRequestException("El correo es obligatorio");
        }
    }

    private void validarDatosPerfil(PerfilClienteUpdateDTO dto) {
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

    private void validarPasswordObligatoria(String password) {
        if (password == null || password.isBlank()) {
            throw new BadRequestException("La contraseña es obligatoria");
        }

        validarLongitudPassword(password);
    }

    private void validarLongitudPassword(String password) {
        if (password.length() < 6) {
            throw new BadRequestException("La contraseña debe tener al menos 6 caracteres");
        }
    }

    private void validarEmailDuplicado(String email) {
        if (usuarioRepository.existsByEmailIgnoreCase(email)) {
            throw new ConflictException("Ya existe un usuario con ese correo");
        }
    }

    private void validarEmailDuplicadoEnEdicion(Long id, String email) {
        usuarioRepository.findByEmailIgnoreCase(email).filter(usuario -> !usuario.getId().equals(id))
                .ifPresent(usuario -> {
                    throw new ConflictException("Ya existe un usuario con ese correo");
                });
    }

    private String normalizarEmail(String email) {
        return email.trim().toLowerCase();
    }

    private String limpiarTelefono(String telefono) {
        return telefono == null || telefono.isBlank() ? null : telefono.trim();
    }
}