package com.devsv.cinemark_api.services;

import com.devsv.cinemark_api.dto.FuncionDTO;
import com.devsv.cinemark_api.entities.Funcion;
import com.devsv.cinemark_api.entities.Pelicula;
import com.devsv.cinemark_api.entities.Sala;
import com.devsv.cinemark_api.exceptions.BadRequestException;
import com.devsv.cinemark_api.exceptions.ConflictException;
import com.devsv.cinemark_api.exceptions.ResourceNotFoundException;
import com.devsv.cinemark_api.interfaces.IFuncionService;
import com.devsv.cinemark_api.mappers.FuncionMapper;
import com.devsv.cinemark_api.repository.FuncionRepository;
import com.devsv.cinemark_api.repository.PeliculaRepository;
import com.devsv.cinemark_api.repository.SalaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class FuncionService implements IFuncionService {

    private final FuncionRepository funcionRepository;
    private final PeliculaRepository peliculaRepository;
    private final SalaRepository salaRepository;
    private final FuncionMapper funcionMapper;

    @Override
    @Transactional(readOnly = true)
    public List<FuncionDTO> findAll() {
        return funcionMapper.toDtoList(funcionRepository.findAll());
    }

    @Override
    @Transactional(readOnly = true)
    public FuncionDTO findById(Long id) {
        return funcionMapper.toDTO(obtenerFuncion(id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<FuncionDTO> findByPelicula(Long peliculaId) {
        if (!peliculaRepository.existsById(peliculaId)) {
            throw new ResourceNotFoundException("Película no encontrada con id: " + peliculaId);
        }

        return funcionMapper.toDtoList(funcionRepository.findByPeliculaId(peliculaId));
    }

    @Override
    @Transactional(readOnly = true)
    public List<FuncionDTO> findBySala(Long salaId) {
        if (!salaRepository.existsById(salaId)) {
            throw new ResourceNotFoundException("Sala no encontrada con id: " + salaId);
        }

        return funcionMapper.toDtoList(funcionRepository.findBySalaId(salaId));
    }

    @Override
    @Transactional(readOnly = true)
    public List<FuncionDTO> findByFecha(LocalDate fecha) {
        return funcionMapper.toDtoList(funcionRepository.findByFecha(fecha));
    }

    @Override
    @Transactional
    public FuncionDTO create(FuncionDTO dto) {
        validarHorario(dto);

        Pelicula pelicula = obtenerPelicula(dto.getPeliculaId());
        Sala sala = obtenerSala(dto.getSalaId());

        validarFechaExhibicion(pelicula, dto.getFecha());
        validarConflictoHorario(null, dto.getSalaId(), dto.getFecha(), dto);

        Funcion funcion = funcionMapper.toEntity(dto);
        funcion.setId(null);
        funcion.setPelicula(pelicula);
        funcion.setSala(sala);

        return funcionMapper.toDTO(funcionRepository.save(funcion));
    }

    @Override
    @Transactional
    public FuncionDTO update(Long id, FuncionDTO dto) {
        Funcion funcion = obtenerFuncion(id);

        validarHorario(dto);

        Pelicula pelicula = obtenerPelicula(dto.getPeliculaId());
        Sala sala = obtenerSala(dto.getSalaId());

        validarFechaExhibicion(pelicula, dto.getFecha());
        validarConflictoHorario(id, dto.getSalaId(), dto.getFecha(), dto);

        funcion.setPelicula(pelicula);
        funcion.setSala(sala);
        funcion.setFecha(dto.getFecha());
        funcion.setHoraInicio(dto.getHoraInicio());
        funcion.setHoraFin(dto.getHoraFin());
        funcion.setPrecio(dto.getPrecio());
        funcion.setTipo(dto.getTipo());
        funcion.setEstado(dto.getEstado());

        return funcionMapper.toDTO(funcionRepository.save(funcion));
    }

    @Override
    @Transactional
    public void delete(Long id) {
        funcionRepository.delete(obtenerFuncion(id));
    }

    private Funcion obtenerFuncion(Long id) {
        return funcionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Función no encontrada con id: " + id));
    }

    private Pelicula obtenerPelicula(Long id) {
        return peliculaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Película no encontrada con id: " + id));
    }

    private Sala obtenerSala(Long id) {
        return salaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Sala no encontrada con id: " + id));
    }

    private void validarFechaExhibicion(Pelicula pelicula, LocalDate fechaFuncion) {
        if (fechaFuncion == null) {
            return;
        }

        if (pelicula.getFechaEstreno() != null && fechaFuncion.isBefore(pelicula.getFechaEstreno())) {
            throw new BadRequestException("La función no puede programarse antes de la fecha de estreno de la película");
        }

        if (pelicula.getFechaFinExhibicion() != null && fechaFuncion.isAfter(pelicula.getFechaFinExhibicion())) {
            throw new BadRequestException("La función no puede programarse después de la fecha final de exhibición de la película");
        }
    }

    private void validarHorario(FuncionDTO dto) {
        if (dto.getHoraInicio() == null || dto.getHoraFin() == null) {
            throw new BadRequestException("La hora de inicio y la hora de fin son obligatorias");
        }

        if (!dto.getHoraFin().isAfter(dto.getHoraInicio())) {
            throw new BadRequestException("La hora de fin debe ser posterior a la hora de inicio");
        }
    }

    private void validarConflictoHorario(Long funcionId, Long salaId, LocalDate fecha, FuncionDTO dto) {
        boolean conflicto = funcionRepository.findBySalaIdAndFecha(salaId, fecha).stream()
                .filter(funcion -> funcionId == null || !funcion.getId().equals(funcionId))
                .anyMatch(funcion -> dto.getHoraInicio().isBefore(funcion.getHoraFin()) &&
                        dto.getHoraFin().isAfter(funcion.getHoraInicio()));

        if (conflicto) {
            throw new ConflictException("La sala ya tiene una función programada en ese horario");
        }
    }
}