package com.devsv.cinemark_api.services;

import com.devsv.cinemark_api.dto.GeneroDTO;
import com.devsv.cinemark_api.entities.Genero;
import com.devsv.cinemark_api.exceptions.ConflictException;
import com.devsv.cinemark_api.exceptions.ResourceNotFoundException;
import com.devsv.cinemark_api.interfaces.IGeneroService;
import com.devsv.cinemark_api.mappers.GeneroMapper;
import com.devsv.cinemark_api.repository.GeneroRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GeneroService implements IGeneroService {

    private final GeneroRepository generoRepository;
    private final GeneroMapper generoMapper;

    @Override
    @Transactional(readOnly = true)
    public List<GeneroDTO> findAll() {
        return generoMapper.toDtoList(generoRepository.findAll());
    }

    @Override
    @Transactional(readOnly = true)
    public GeneroDTO findById(Long id) {
        return generoMapper.toDTO(obtenerGenero(id));
    }

    @Override
    @Transactional
    public GeneroDTO create(GeneroDTO generoDTO) {
        if (generoRepository.existsByNombreIgnoreCase(generoDTO.getNombre())) {
            throw new ConflictException("Ya existe un género con ese nombre");
        }

        Genero genero = generoMapper.toEntity(generoDTO);
        genero.setId(null);

        return generoMapper.toDTO(generoRepository.save(genero));
    }

    @Override
    @Transactional
    public GeneroDTO update(Long id, GeneroDTO generoDTO) {
        Genero genero = obtenerGenero(id);

        generoRepository.findByNombreIgnoreCase(generoDTO.getNombre())
                .filter(existente -> !existente.getId().equals(id)).ifPresent(existente -> {
                    throw new ConflictException("Ya existe un género con ese nombre");
                });

        genero.setNombre(generoDTO.getNombre());

        return generoMapper.toDTO(generoRepository.save(genero));
    }

    @Override
    @Transactional
    public void delete(Long id) {
        generoRepository.delete(obtenerGenero(id));
    }

    private Genero obtenerGenero(Long id) {
        return generoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Género no encontrado con id: " + id));
    }
}