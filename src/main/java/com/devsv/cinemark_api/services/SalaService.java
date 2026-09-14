package com.devsv.cinemark_api.services;

import com.devsv.cinemark_api.dto.SalaDTO;
import com.devsv.cinemark_api.entities.Sala;
import com.devsv.cinemark_api.exceptions.ConflictException;
import com.devsv.cinemark_api.exceptions.ResourceNotFoundException;
import com.devsv.cinemark_api.interfaces.ISalaService;
import com.devsv.cinemark_api.mappers.SalaMapper;
import com.devsv.cinemark_api.repository.SalaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SalaService implements ISalaService {

    private final SalaRepository salaRepository;
    private final SalaMapper salaMapper;

    @Override
    @Transactional(readOnly = true)
    public List<SalaDTO> findAll() {
        return salaMapper.toDtoList(salaRepository.findAll());
    }

    @Override
    @Transactional(readOnly = true)
    public SalaDTO findById(Long id) {
        return salaMapper.toDTO(obtenerSala(id));
    }

    @Override
    @Transactional
    public SalaDTO create(SalaDTO dto) {
        if (salaRepository.existsByNombreIgnoreCase(dto.getNombre())) {
            throw new ConflictException("Ya existe una sala con ese nombre");
        }

        Sala sala = salaMapper.toEntity(dto);
        sala.setId(null);

        return salaMapper.toDTO(salaRepository.save(sala));
    }

    @Override
    @Transactional
    public SalaDTO update(Long id, SalaDTO dto) {
        Sala sala = obtenerSala(id);

        salaRepository.findByNombreIgnoreCase(dto.getNombre()).filter(encontrada -> !encontrada.getId().equals(id))
                .ifPresent(encontrada -> {
                    throw new ConflictException("Ya existe una sala con ese nombre");
                });

        sala.setNombre(dto.getNombre());
        sala.setCapacidad(dto.getCapacidad());
        sala.setTipo(dto.getTipo());
        sala.setEstado(dto.getEstado());

        return salaMapper.toDTO(salaRepository.save(sala));
    }

    @Override
    @Transactional
    public void delete(Long id) {
        salaRepository.delete(obtenerSala(id));
    }

    private Sala obtenerSala(Long id) {
        return salaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Sala no encontrada con id: " + id));
    }
}