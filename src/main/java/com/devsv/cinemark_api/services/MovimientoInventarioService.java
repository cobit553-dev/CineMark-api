package com.devsv.cinemark_api.services;

import com.devsv.cinemark_api.dto.MovimientoInventarioDTO;
import com.devsv.cinemark_api.entities.Inventario;
import com.devsv.cinemark_api.entities.MovimientoInventario;
import com.devsv.cinemark_api.entities.Usuario;
import com.devsv.cinemark_api.enums.TipoMovimientoInventario;
import com.devsv.cinemark_api.exceptions.BadRequestException;
import com.devsv.cinemark_api.exceptions.ConflictException;
import com.devsv.cinemark_api.exceptions.ResourceNotFoundException;
import com.devsv.cinemark_api.interfaces.IMovimientoInventarioService;
import com.devsv.cinemark_api.mappers.MovimientoInventarioMapper;
import com.devsv.cinemark_api.repository.InventarioRepository;
import com.devsv.cinemark_api.repository.MovimientoInventarioRepository;
import com.devsv.cinemark_api.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MovimientoInventarioService implements IMovimientoInventarioService {

    private final MovimientoInventarioRepository movimientoRepository;
    private final InventarioRepository inventarioRepository;
    private final UsuarioRepository usuarioRepository;
    private final MovimientoInventarioMapper movimientoMapper;

    @Override
    @Transactional(readOnly = true)
    public List<MovimientoInventarioDTO> findAll() {
        return movimientoMapper.toDtoList(movimientoRepository.findAllByOrderByFechaDesc());
    }

    @Override
    @Transactional(readOnly = true)
    public MovimientoInventarioDTO findById(Long id) {
        return movimientoMapper.toDTO(obtenerMovimiento(id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<MovimientoInventarioDTO> findByInventario(Long inventarioId) {
        if (!inventarioRepository.existsById(inventarioId)) {
            throw new ResourceNotFoundException("Inventario no encontrado con id: " + inventarioId);
        }

        return movimientoMapper.toDtoList(movimientoRepository.findByInventarioId(inventarioId));
    }

    @Override
    @Transactional
    public MovimientoInventarioDTO create(MovimientoInventarioDTO dto, String emailUsuario) {
        validarDTO(dto);

        Usuario usuario = usuarioRepository.findByEmailIgnoreCase(emailUsuario)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario autenticado no encontrado"));

        return registrarMovimiento(dto, usuario);
    }

    @Override
    @Transactional
    public MovimientoInventarioDTO createSistema(MovimientoInventarioDTO dto) {
        validarDTO(dto);
        return registrarMovimiento(dto, null);
    }

    private MovimientoInventarioDTO registrarMovimiento(MovimientoInventarioDTO dto, Usuario usuario) {
        Inventario inventario = inventarioRepository.findWithLockById(dto.getInventarioId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Inventario no encontrado con id: " + dto.getInventarioId()));

        actualizarStock(inventario, dto.getTipo(), dto.getCantidad());

        LocalDateTime ahora = LocalDateTime.now();

        inventario.setFechaActualizacion(ahora);
        inventarioRepository.save(inventario);

        MovimientoInventario movimiento = MovimientoInventario.builder().inventario(inventario).usuario(usuario)
                .tipo(dto.getTipo()).cantidad(dto.getCantidad()).motivo(dto.getMotivo().trim()).fecha(ahora).build();

        return movimientoMapper.toDTO(movimientoRepository.save(movimiento));
    }

    private MovimientoInventario obtenerMovimiento(Long id) {
        return movimientoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Movimiento no encontrado con id: " + id));
    }

    private void validarDTO(MovimientoInventarioDTO dto) {
        if (dto == null) {
            throw new BadRequestException("Los datos del movimiento son obligatorios");
        }

        if (dto.getInventarioId() == null) {
            throw new BadRequestException("El inventario es obligatorio");
        }

        if (dto.getTipo() == null) {
            throw new BadRequestException("El tipo de movimiento es obligatorio");
        }

        if (dto.getCantidad() == null) {
            throw new BadRequestException("La cantidad es obligatoria");
        }

        if (dto.getMotivo() == null || dto.getMotivo().isBlank()) {
            throw new BadRequestException("El motivo del movimiento es obligatorio");
        }
    }

    private void actualizarStock(Inventario inventario, TipoMovimientoInventario tipo, Integer cantidad) {
        if (cantidad < 0) {
            throw new BadRequestException("La cantidad no puede ser negativa");
        }

        if (cantidad == 0 && tipo != TipoMovimientoInventario.AJUSTE) {
            throw new BadRequestException("La cantidad debe ser mayor que cero");
        }

        int stockActual = inventario.getStockActual() != null ? inventario.getStockActual() : 0;

        switch (tipo) {
            case ENTRADA -> inventario.setStockActual(stockActual + cantidad);

            case SALIDA -> {
                if (cantidad > stockActual) {
                    throw new ConflictException("Stock insuficiente. Disponible: " + stockActual);
                }

                inventario.setStockActual(stockActual - cantidad);
            }

            case AJUSTE -> inventario.setStockActual(cantidad);
        }
    }
}