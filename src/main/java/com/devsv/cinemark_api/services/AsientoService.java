package com.devsv.cinemark_api.services;

import com.devsv.cinemark_api.dto.AsientoDTO;
import com.devsv.cinemark_api.dto.AsientoDisponibilidadDTO;
import com.devsv.cinemark_api.dto.GenerarAsientosRequestDTO;
import com.devsv.cinemark_api.entities.Asiento;
import com.devsv.cinemark_api.entities.Boleto;
import com.devsv.cinemark_api.entities.Funcion;
import com.devsv.cinemark_api.entities.Sala;
import com.devsv.cinemark_api.enums.EstadoBoleto;
import com.devsv.cinemark_api.exceptions.BadRequestException;
import com.devsv.cinemark_api.exceptions.ConflictException;
import com.devsv.cinemark_api.exceptions.ResourceNotFoundException;
import com.devsv.cinemark_api.interfaces.IAsientoService;
import com.devsv.cinemark_api.mappers.AsientoMapper;
import com.devsv.cinemark_api.repository.AsientoRepository;
import com.devsv.cinemark_api.repository.BoletoRepository;
import com.devsv.cinemark_api.repository.FuncionRepository;
import com.devsv.cinemark_api.repository.SalaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AsientoService implements IAsientoService {

    private static final Set<String> TIPOS_PERMITIDOS = Set.of("NORMAL", "VIP", "DISCAPACITADO");

    private final AsientoRepository asientoRepository;
    private final SalaRepository salaRepository;
    private final FuncionRepository funcionRepository;
    private final BoletoRepository boletoRepository;
    private final AsientoMapper asientoMapper;
    private final ReservaBoletoService reservaBoletoService;

    @Override
    @Transactional(readOnly = true)
    public List<AsientoDTO> findAll() {
        return asientoMapper.toDtoList(asientoRepository.findAll());
    }

    @Override
    @Transactional(readOnly = true)
    public AsientoDTO findById(Long id) {
        Asiento asiento = obtenerAsiento(id);
        return asientoMapper.toDTO(asiento);
    }

    @Override
    @Transactional(readOnly = true)
    public List<AsientoDTO> findBySala(Long salaId) {
        if (!salaRepository.existsById(salaId)) {
            throw new ResourceNotFoundException("Sala no encontrada con id: " + salaId);
        }

        return asientoMapper.toDtoList(asientoRepository.findBySalaId(salaId));
    }

    @Override
    @Transactional
    public List<AsientoDisponibilidadDTO> findDisponibilidadByFuncion(Long funcionId) {
        reservaBoletoService.liberarReservasVencidas();

        Funcion funcion = funcionRepository.findById(funcionId)
                .orElseThrow(() -> new ResourceNotFoundException("Función no encontrada con id: " + funcionId));

        List<Asiento> asientos = asientoRepository.findBySalaId(funcion.getSala().getId());
        List<Boleto> boletos = boletoRepository.findByFuncionId(funcionId);

        Set<Long> asientosOcupados = boletos.stream().filter(boleto -> boleto.getEstado() == EstadoBoleto.RESERVADO ||
                        boleto.getEstado() == EstadoBoleto.VENDIDO).map(boleto -> boleto.getAsiento().getId())
                .collect(Collectors.toSet());

        return asientos.stream().map(asiento -> crearDisponibilidad(asiento, asientosOcupados)).toList();
    }

    @Override
    @Transactional
    public AsientoDTO create(AsientoDTO dto) {
        Sala sala = obtenerSala(dto.getSalaId());

        String fila = normalizarFila(dto.getFila());
        String tipo = normalizarTipo(dto.getTipo());

        validarAsientoDuplicado(dto.getSalaId(), fila, dto.getNumero());

        Asiento asiento = asientoMapper.toEntity(dto);
        asiento.setId(null);
        asiento.setSala(sala);
        asiento.setFila(fila);
        asiento.setTipo(tipo);

        if (asiento.getEstado() == null) {
            asiento.setEstado(true);
        }

        return asientoMapper.toDTO(asientoRepository.save(asiento));
    }

    @Override
    @Transactional
    public List<AsientoDTO> generar(GenerarAsientosRequestDTO dto) {
        Sala sala = obtenerSala(dto.getSalaId());

        char filaInicial = normalizarFila(dto.getFilaInicial()).charAt(0);
        char filaFinal = normalizarFila(dto.getFilaFinal()).charAt(0);

        if (filaInicial > filaFinal) {
            throw new BadRequestException("La fila inicial no puede ser posterior a la fila final");
        }

        String tipo = normalizarTipo(dto.getTipo());
        boolean estado = dto.getEstado() == null || dto.getEstado();

        List<Asiento> nuevos = new ArrayList<>();

        for (char fila = filaInicial; fila <= filaFinal; fila++) {
            String nombreFila = String.valueOf(fila);

            for (int numero = 1; numero <= dto.getCantidadPorFila(); numero++) {
                if (asientoRepository.existsBySalaIdAndFilaIgnoreCaseAndNumero(sala.getId(), nombreFila, numero)) {
                    continue;
                }

                nuevos.add(Asiento.builder().sala(sala).fila(nombreFila).numero(numero).tipo(tipo).estado(estado)
                        .build());
            }
        }

        if (nuevos.isEmpty()) {
            return List.of();
        }

        return asientoMapper.toDtoList(asientoRepository.saveAll(nuevos));
    }

    @Override
    @Transactional
    public AsientoDTO update(Long id, AsientoDTO dto) {
        Asiento asiento = obtenerAsiento(id);
        Sala sala = obtenerSala(dto.getSalaId());

        String fila = normalizarFila(dto.getFila());
        String tipo = normalizarTipo(dto.getTipo());

        asientoRepository.findBySalaIdAndFilaIgnoreCaseAndNumero(dto.getSalaId(), fila, dto.getNumero())
                .filter(existente -> !existente.getId().equals(id)).ifPresent(existente -> {
                    throw new ConflictException("Ya existe ese asiento en la sala");
                });

        asiento.setSala(sala);
        asiento.setFila(fila);
        asiento.setNumero(dto.getNumero());
        asiento.setTipo(tipo);

        if (dto.getEstado() != null) {
            asiento.setEstado(dto.getEstado());
        }

        return asientoMapper.toDTO(asientoRepository.save(asiento));
    }

    @Override
    @Transactional
    public void delete(Long id) {
        Asiento asiento = obtenerAsiento(id);
        asientoRepository.delete(asiento);
    }

    private AsientoDisponibilidadDTO crearDisponibilidad(Asiento asiento, Set<Long> asientosOcupados) {
        boolean habilitado = Boolean.TRUE.equals(asiento.getEstado());
        boolean ocupado = asientosOcupados.contains(asiento.getId());

        return AsientoDisponibilidadDTO.builder().asientoId(asiento.getId()).salaId(asiento.getSala().getId())
                .salaNombre(asiento.getSala().getNombre()).fila(asiento.getFila()).numero(asiento.getNumero())
                .tipo(asiento.getTipo()).estado(asiento.getEstado()).disponible(habilitado && !ocupado).build();
    }

    private Asiento obtenerAsiento(Long id) {
        return asientoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Asiento no encontrado con id: " + id));
    }

    private Sala obtenerSala(Long salaId) {
        return salaRepository.findById(salaId)
                .orElseThrow(() -> new ResourceNotFoundException("Sala no encontrada con id: " + salaId));
    }

    private void validarAsientoDuplicado(Long salaId, String fila, Integer numero) {
        if (asientoRepository.existsBySalaIdAndFilaIgnoreCaseAndNumero(salaId, fila, numero)) {
            throw new ConflictException("Ya existe ese asiento en la sala");
        }
    }

    private String normalizarFila(String fila) {
        if (fila == null || fila.isBlank()) {
            throw new BadRequestException("La fila del asiento es obligatoria");
        }

        return fila.trim().toUpperCase();
    }

    private String normalizarTipo(String tipo) {
        if (tipo == null || tipo.isBlank()) {
            throw new BadRequestException("El tipo de asiento es obligatorio");
        }

        String tipoNormalizado = tipo.trim().toUpperCase();

        if (!TIPOS_PERMITIDOS.contains(tipoNormalizado)) {
            throw new BadRequestException("Tipo de asiento inválido. Use NORMAL, VIP o DISCAPACITADO");
        }

        return tipoNormalizado;
    }
}