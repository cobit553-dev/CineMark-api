package com.devsv.cinemark_api.services;

import com.devsv.cinemark_api.dto.IncidenciaDTO;
import com.devsv.cinemark_api.entities.Incidencia;
import com.devsv.cinemark_api.entities.Sala;
import com.devsv.cinemark_api.enums.EstadoIncidencia;
import com.devsv.cinemark_api.exceptions.BadRequestException;
import com.devsv.cinemark_api.exceptions.ResourceNotFoundException;
import com.devsv.cinemark_api.interfaces.IIncidenciaService;
import com.devsv.cinemark_api.mappers.IncidenciaMapper;
import com.devsv.cinemark_api.repository.IncidenciaRepository;
import com.devsv.cinemark_api.repository.SalaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class IncidenciaService implements IIncidenciaService {

    private final IncidenciaRepository incidenciaRepository;
    private final SalaRepository salaRepository;
    private final IncidenciaMapper incidenciaMapper;

    @Override
    @Transactional(readOnly = true)
    public List<IncidenciaDTO> findAll() {
        return incidenciaRepository.findAllByOrderByFechaDesc().stream().map(incidenciaMapper::toDTO).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public IncidenciaDTO findById(Long id) {
        return incidenciaMapper.toDTO(obtenerIncidencia(id));
    }

    @Override
    @Transactional
    public IncidenciaDTO create(IncidenciaDTO incidenciaDTO) {
        validarDatos(incidenciaDTO);

        Sala sala = obtenerSala(incidenciaDTO.getSalaId());

        Incidencia incidencia = new Incidencia();
        incidencia.setSala(sala);
        incidencia.setTitulo(incidenciaDTO.getTitulo().trim());
        incidencia.setDescripcion(incidenciaDTO.getDescripcion().trim());
        incidencia.setFecha(LocalDateTime.now());
        incidencia.setEstado(incidenciaDTO.getEstado() != null ? incidenciaDTO.getEstado() : EstadoIncidencia.ABIERTA);

        return incidenciaMapper.toDTO(incidenciaRepository.save(incidencia));
    }

    @Override
    @Transactional
    public IncidenciaDTO update(Long id, IncidenciaDTO incidenciaDTO) {
        validarDatos(incidenciaDTO);

        Incidencia incidencia = obtenerIncidencia(id);
        Sala sala = obtenerSala(incidenciaDTO.getSalaId());

        incidencia.setSala(sala);
        incidencia.setTitulo(incidenciaDTO.getTitulo().trim());
        incidencia.setDescripcion(incidenciaDTO.getDescripcion().trim());

        if (incidenciaDTO.getEstado() != null) {
            incidencia.setEstado(incidenciaDTO.getEstado());
        }

        return incidenciaMapper.toDTO(incidenciaRepository.save(incidencia));
    }

    @Override
    @Transactional(readOnly = true)
    public List<IncidenciaDTO> findBySala(Long salaId) {
        if (!salaRepository.existsById(salaId)) {
            throw new ResourceNotFoundException("Sala no encontrada con id: " + salaId);
        }

        return incidenciaRepository.findBySalaId(salaId).stream().map(incidenciaMapper::toDTO).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<IncidenciaDTO> findByEstado(EstadoIncidencia estado) {
        if (estado == null) {
            throw new BadRequestException("El estado es obligatorio");
        }

        return incidenciaRepository.findByEstado(estado).stream().map(incidenciaMapper::toDTO).toList();
    }

    private Incidencia obtenerIncidencia(Long id) {
        return incidenciaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Incidencia no encontrada con id: " + id));
    }

    private Sala obtenerSala(Long id) {
        return salaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Sala no encontrada con id: " + id));
    }

    private void validarDatos(IncidenciaDTO incidenciaDTO) {
        if (incidenciaDTO == null) {
            throw new BadRequestException("Los datos de la incidencia son obligatorios");
        }

        if (incidenciaDTO.getSalaId() == null) {
            throw new BadRequestException("La sala es obligatoria");
        }

        if (incidenciaDTO.getTitulo() == null || incidenciaDTO.getTitulo().isBlank()) {
            throw new BadRequestException("El título es obligatorio");
        }

        if (incidenciaDTO.getDescripcion() == null || incidenciaDTO.getDescripcion().isBlank()) {
            throw new BadRequestException("La descripción es obligatoria");
        }
    }
}