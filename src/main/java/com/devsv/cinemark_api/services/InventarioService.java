package com.devsv.cinemark_api.services;

import com.devsv.cinemark_api.dto.InventarioDTO;
import com.devsv.cinemark_api.entities.Inventario;
import com.devsv.cinemark_api.entities.Producto;
import com.devsv.cinemark_api.exceptions.BadRequestException;
import com.devsv.cinemark_api.exceptions.ConflictException;
import com.devsv.cinemark_api.exceptions.ResourceNotFoundException;
import com.devsv.cinemark_api.interfaces.IInventarioService;
import com.devsv.cinemark_api.mappers.InventarioMapper;
import com.devsv.cinemark_api.repository.InventarioRepository;
import com.devsv.cinemark_api.repository.ProductoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class InventarioService implements IInventarioService {

    private final InventarioRepository inventarioRepository;
    private final ProductoRepository productoRepository;
    private final InventarioMapper inventarioMapper;

    @Override
    @Transactional(readOnly = true)
    public List<InventarioDTO> findAll() {
        return inventarioMapper.toDtoList(inventarioRepository.findAll());
    }

    @Override
    @Transactional(readOnly = true)
    public InventarioDTO findById(Long id) {
        return inventarioMapper.toDTO(obtenerInventario(id));
    }

    @Override
    @Transactional(readOnly = true)
    public InventarioDTO findByProducto(Long productoId) {
        Inventario inventario = inventarioRepository.findByProductoId(productoId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Inventario no encontrado para el producto id: " + productoId));

        return inventarioMapper.toDTO(inventario);
    }

    @Override
    @Transactional
    public InventarioDTO create(InventarioDTO dto) {
        validarStock(dto);

        if (inventarioRepository.existsByProductoId(dto.getProductoId())) {
            throw new ConflictException("El producto ya tiene un inventario registrado");
        }

        Producto producto = obtenerProducto(dto.getProductoId());

        Inventario inventario = inventarioMapper.toEntity(dto);
        inventario.setId(null);
        inventario.setProducto(producto);
        inventario.setFechaActualizacion(LocalDateTime.now());

        return inventarioMapper.toDTO(inventarioRepository.save(inventario));
    }

    @Override
    @Transactional
    public InventarioDTO update(Long id, InventarioDTO dto) {
        validarStock(dto);

        Inventario inventario = obtenerInventario(id);
        Producto producto = obtenerProducto(dto.getProductoId());

        inventarioRepository.findByProductoId(dto.getProductoId()).filter(existente -> !existente.getId().equals(id))
                .ifPresent(existente -> {
                    throw new ConflictException("El producto ya tiene otro inventario registrado");
                });

        inventario.setProducto(producto);
        inventario.setStockActual(dto.getStockActual());
        inventario.setStockMinimo(dto.getStockMinimo());
        inventario.setFechaActualizacion(LocalDateTime.now());

        return inventarioMapper.toDTO(inventarioRepository.save(inventario));
    }

    @Override
    @Transactional
    public void delete(Long id) {
        inventarioRepository.delete(obtenerInventario(id));
    }

    private Inventario obtenerInventario(Long id) {
        return inventarioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Inventario no encontrado con id: " + id));
    }

    private Producto obtenerProducto(Long id) {
        return productoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado con id: " + id));
    }

    private void validarStock(InventarioDTO dto) {
        if (dto.getStockActual() == null) {
            throw new BadRequestException("El stock actual es obligatorio");
        }

        if (dto.getStockActual() < 0) {
            throw new BadRequestException("El stock actual no puede ser negativo");
        }

        if (dto.getStockMinimo() == null) {
            throw new BadRequestException("El stock mínimo es obligatorio");
        }

        if (dto.getStockMinimo() < 0) {
            throw new BadRequestException("El stock mínimo no puede ser negativo");
        }
    }
}