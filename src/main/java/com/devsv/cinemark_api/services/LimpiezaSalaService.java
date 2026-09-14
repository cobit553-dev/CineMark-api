package com.devsv.cinemark_api.services;

import com.devsv.cinemark_api.dto.LimpiezaSalaDTO;
import com.devsv.cinemark_api.entities.LimpiezaSala;
import com.devsv.cinemark_api.entities.Sala;
import com.devsv.cinemark_api.enums.EstadoLimpieza;
import com.devsv.cinemark_api.exceptions.BadRequestException;
import com.devsv.cinemark_api.exceptions.ResourceNotFoundException;
import com.devsv.cinemark_api.interfaces.ILimpiezaSalaService;
import com.devsv.cinemark_api.mappers.LimpiezaSalaMapper;
import com.devsv.cinemark_api.repository.LimpiezaSalaRepository;
import com.devsv.cinemark_api.repository.SalaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class LimpiezaSalaService implements ILimpiezaSalaService {

    private final LimpiezaSalaRepository limpiezaSalaRepository;
    private final SalaRepository salaRepository;
    private final LimpiezaSalaMapper limpiezaSalaMapper;

    @Override
    @Transactional(readOnly = true)
    public List<LimpiezaSalaDTO> findAll() {
        return limpiezaSalaRepository.findAllByOrderByFechaHoraDesc().stream().map(limpiezaSalaMapper::toDTO).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public LimpiezaSalaDTO findById(Long id) {
        return limpiezaSalaMapper.toDTO(obtenerLimpieza(id));
    }

    @Override
    @Transactional
    public LimpiezaSalaDTO create(LimpiezaSalaDTO limpiezaSalaDTO) {
        validarDatos(limpiezaSalaDTO);

        Sala sala = obtenerSala(limpiezaSalaDTO.getSalaId());

        LimpiezaSala limpieza = new LimpiezaSala();
        limpieza.setSala(sala);
        limpieza.setFechaHora(
                limpiezaSalaDTO.getFechaHora() != null ? limpiezaSalaDTO.getFechaHora() : LocalDateTime.now());
        limpieza.setResponsable(limpiezaSalaDTO.getResponsable().trim());
        limpieza.setObservaciones(
                limpiezaSalaDTO.getObservaciones() != null ? limpiezaSalaDTO.getObservaciones().trim() : null);
        limpieza.setEstado(
                limpiezaSalaDTO.getEstado() != null ? limpiezaSalaDTO.getEstado() : EstadoLimpieza.PENDIENTE);

        return limpiezaSalaMapper.toDTO(limpiezaSalaRepository.save(limpieza));
    }

    @Override
    @Transactional
    public LimpiezaSalaDTO update(Long id, LimpiezaSalaDTO limpiezaSalaDTO) {
        validarDatos(limpiezaSalaDTO);

        LimpiezaSala limpieza = obtenerLimpieza(id);
        Sala sala = obtenerSala(limpiezaSalaDTO.getSalaId());

        limpieza.setSala(sala);

        if (limpiezaSalaDTO.getFechaHora() != null) {
            limpieza.setFechaHora(limpiezaSalaDTO.getFechaHora());
        }

        limpieza.setResponsable(limpiezaSalaDTO.getResponsable().trim());
        limpieza.setObservaciones(
                limpiezaSalaDTO.getObservaciones() != null ? limpiezaSalaDTO.getObservaciones().trim() : null);

        if (limpiezaSalaDTO.getEstado() != null) {
            limpieza.setEstado(limpiezaSalaDTO.getEstado());
        }

        return limpiezaSalaMapper.toDTO(limpiezaSalaRepository.save(limpieza));
    }

    @Override
    @Transactional(readOnly = true)
    public List<LimpiezaSalaDTO> findBySala(Long salaId) {
        if (!salaRepository.existsById(salaId)) {
            throw new ResourceNotFoundException("Sala no encontrada con id: " + salaId);
        }

        return limpiezaSalaRepository.findBySalaIdOrderByFechaHoraDesc(salaId).stream().map(limpiezaSalaMapper::toDTO)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<LimpiezaSalaDTO> findByEstado(EstadoLimpieza estado) {
        if (estado == null) {
            throw new BadRequestException("El estado es obligatorio");
        }

        return limpiezaSalaRepository.findByEstadoOrderByFechaHoraDesc(estado).stream().map(limpiezaSalaMapper::toDTO)
                .toList();
    }

    private LimpiezaSala obtenerLimpieza(Long id) {
        return limpiezaSalaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Registro de limpieza no encontrado con id: " + id));
    }

    private Sala obtenerSala(Long id) {
        return salaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Sala no encontrada con id: " + id));
    }

    private void validarDatos(LimpiezaSalaDTO limpiezaSalaDTO) {
        if (limpiezaSalaDTO == null) {
            throw new BadRequestException("Los datos de la limpieza son obligatorios");
        }

        if (limpiezaSalaDTO.getSalaId() == null) {
            throw new BadRequestException("La sala es obligatoria");
        }

        if (limpiezaSalaDTO.getResponsable() == null || limpiezaSalaDTO.getResponsable().isBlank()) {
            throw new BadRequestException("El responsable es obligatorio");
        }
    }
}