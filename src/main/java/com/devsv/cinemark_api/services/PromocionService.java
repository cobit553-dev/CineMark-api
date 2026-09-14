package com.devsv.cinemark_api.services;

import com.devsv.cinemark_api.dto.PromocionDTO;
import com.devsv.cinemark_api.entities.Promocion;
import com.devsv.cinemark_api.enums.EstadoPromocion;
import com.devsv.cinemark_api.exceptions.BadRequestException;
import com.devsv.cinemark_api.exceptions.ConflictException;
import com.devsv.cinemark_api.exceptions.ResourceNotFoundException;
import com.devsv.cinemark_api.interfaces.IPromocionService;
import com.devsv.cinemark_api.mappers.PromocionMapper;
import com.devsv.cinemark_api.repository.PromocionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PromocionService implements IPromocionService {

    private final PromocionRepository promocionRepository;
    private final PromocionMapper promocionMapper;

    @Override
    @Transactional(readOnly = true)
    public List<PromocionDTO> findAll() {

        return promocionMapper.toDtoList(promocionRepository.findAllByOrderByFechaInicioDesc());
    }

    @Override
    @Transactional(readOnly = true)
    public PromocionDTO findById(Long id) {

        Promocion promocion = promocionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Promoción no encontrada con id: " + id));

        return promocionMapper.toDTO(promocion);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PromocionDTO> findVigentes() {

        LocalDate hoy = LocalDate.now();

        List<Promocion> promociones = promocionRepository.findByEstadoAndFechaInicioLessThanEqualAndFechaFinGreaterThanEqual(EstadoPromocion.ACTIVA, hoy, hoy)
                .stream()
                .filter(promocion -> promocion.getDiaSemana() == null || promocion.getDiaSemana() == hoy.getDayOfWeek())
                .toList();

        return promocionMapper.toDtoList(promociones);
    }

    @Override
    @Transactional
    public PromocionDTO create(PromocionDTO dto) {

        validar(dto);

        Promocion promocion = new Promocion();

        aplicarDatos(promocion, dto);

        if (promocion.getEstado() == null) {
            promocion.setEstado(EstadoPromocion.ACTIVA);
        }

        Promocion guardada = promocionRepository.save(promocion);

        return promocionMapper.toDTO(guardada);
    }

    @Override
    @Transactional
    public PromocionDTO update(Long id, PromocionDTO dto) {

        validar(dto);

        Promocion promocion = promocionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Promoción no encontrada con id: " + id));

        aplicarDatos(promocion, dto);

        Promocion actualizada = promocionRepository.save(promocion);

        return promocionMapper.toDTO(actualizada);
    }

    private void aplicarDatos(Promocion promocion, PromocionDTO dto) {

        promocion.setNombre(dto.getNombre().trim());

        promocion.setDescripcion(dto.getDescripcion().trim());

        promocion.setPorcentajeDescuento(dto.getPorcentajeDescuento());

        promocion.setFechaInicio(dto.getFechaInicio());

        promocion.setFechaFin(dto.getFechaFin());

        promocion.setDiaSemana(dto.getDiaSemana());

        promocion.setTipoAplicacion(dto.getTipoAplicacion());

        if (dto.getEstado() != null) {

            promocion.setEstado(dto.getEstado());
        }
    }

    private void validar(PromocionDTO dto) {

        if (dto == null) {

            throw new BadRequestException("Los datos de la promoción son obligatorios");
        }

        if (dto.getNombre() == null || dto.getNombre().isBlank()) {

            throw new BadRequestException("El nombre de la promoción es obligatorio");
        }

        if (dto.getDescripcion() == null || dto.getDescripcion().isBlank()) {

            throw new BadRequestException("La descripción de la promoción es obligatoria");
        }

        if (dto.getPorcentajeDescuento() == null || dto.getPorcentajeDescuento().compareTo(BigDecimal.ZERO) <= 0 ||
                dto.getPorcentajeDescuento().compareTo(BigDecimal.valueOf(99)) > 0) {

            throw new BadRequestException("El porcentaje de descuento debe ser mayor que 0 y menor o igual a 99");
        }

        if (dto.getFechaInicio() == null || dto.getFechaFin() == null) {

            throw new BadRequestException("Las fechas de inicio y fin son obligatorias");
        }

        if (dto.getFechaFin().isBefore(dto.getFechaInicio())) {

            throw new ConflictException("La fecha fin no puede ser anterior a la fecha inicio");
        }

        if (dto.getTipoAplicacion() == null) {

            throw new BadRequestException("El tipo de aplicación de la promoción es obligatorio");
        }
    }
}