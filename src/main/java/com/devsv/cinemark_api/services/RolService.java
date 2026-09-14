package com.devsv.cinemark_api.services;

import com.devsv.cinemark_api.dto.RolDTO;
import com.devsv.cinemark_api.entities.Rol;
import com.devsv.cinemark_api.exceptions.ConflictException;
import com.devsv.cinemark_api.exceptions.ResourceNotFoundException;
import com.devsv.cinemark_api.interfaces.IRolService;
import com.devsv.cinemark_api.mappers.RolMapper;
import com.devsv.cinemark_api.repository.RolRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RolService implements IRolService {

    private final RolRepository rolRepository;
    private final RolMapper rolMapper;

    @Override
    @Transactional(readOnly = true)
    public List<RolDTO> findAll() {
        return rolMapper.toDtoList(rolRepository.findAll());
    }

    @Override
    @Transactional(readOnly = true)
    public RolDTO findById(Long id) {
        return rolMapper.toDTO(obtenerRol(id));
    }

    @Override
    @Transactional
    public RolDTO create(RolDTO dto) {
        if (rolRepository.existsByNombre(dto.getNombre())) {
            throw new ConflictException("El rol ya se encuentra registrado");
        }

        Rol rol = rolMapper.toEntity(dto);
        rol.setId(null);

        if (rol.getEstado() == null) {
            rol.setEstado(true);
        }

        return rolMapper.toDTO(rolRepository.save(rol));
    }

    @Override
    @Transactional
    public RolDTO update(Long id, RolDTO dto) {
        Rol rol = obtenerRol(id);

        rolRepository.findByNombre(dto.getNombre()).filter(encontrado -> !encontrado.getId().equals(id))
                .ifPresent(encontrado -> {
                    throw new ConflictException("El rol ya se encuentra registrado");
                });

        rol.setNombre(dto.getNombre());
        rol.setDescripcion(dto.getDescripcion());

        if (dto.getEstado() != null) {
            rol.setEstado(dto.getEstado());
        }

        return rolMapper.toDTO(rolRepository.save(rol));
    }

    @Override
    @Transactional
    public void delete(Long id) {
        rolRepository.delete(obtenerRol(id));
    }

    private Rol obtenerRol(Long id) {
        return rolRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Rol no encontrado con id: " + id));
    }
}