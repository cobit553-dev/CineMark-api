package com.devsv.cinemark_api.services;

import com.devsv.cinemark_api.dto.CategoriaProductoDTO;
import com.devsv.cinemark_api.entities.CategoriaProducto;
import com.devsv.cinemark_api.exceptions.ConflictException;
import com.devsv.cinemark_api.exceptions.ResourceNotFoundException;
import com.devsv.cinemark_api.interfaces.ICategoriaProductoService;
import com.devsv.cinemark_api.mappers.CategoriaProductoMapper;
import com.devsv.cinemark_api.repository.CategoriaProductoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoriaProductoService implements ICategoriaProductoService {

    private final CategoriaProductoRepository repository;
    private final CategoriaProductoMapper mapper;

    @Override
    @Transactional(readOnly = true)
    public List<CategoriaProductoDTO> findAll() {
        return mapper.toDtoList(repository.findAll());
    }

    @Override
    @Transactional(readOnly = true)
    public CategoriaProductoDTO findById(Long id) {
        return mapper.toDTO(obtenerCategoria(id));
    }

    @Override
    @Transactional
    public CategoriaProductoDTO create(CategoriaProductoDTO dto) {
        if (repository.existsByNombreIgnoreCase(dto.getNombre())) {
            throw new ConflictException("Ya existe una categoría con ese nombre");
        }

        CategoriaProducto categoria = mapper.toEntity(dto);
        categoria.setId(null);

        if (categoria.getEstado() == null) {
            categoria.setEstado(true);
        }

        return mapper.toDTO(repository.save(categoria));
    }

    @Override
    @Transactional
    public CategoriaProductoDTO update(Long id, CategoriaProductoDTO dto) {
        CategoriaProducto categoria = obtenerCategoria(id);

        repository.findByNombreIgnoreCase(dto.getNombre()).filter(existente -> !existente.getId().equals(id))
                .ifPresent(existente -> {
                    throw new ConflictException("Ya existe una categoría con ese nombre");
                });

        categoria.setNombre(dto.getNombre());
        categoria.setDescripcion(dto.getDescripcion());

        if (dto.getEstado() != null) {
            categoria.setEstado(dto.getEstado());
        }

        return mapper.toDTO(repository.save(categoria));
    }

    @Override
    @Transactional
    public void delete(Long id) {
        repository.delete(obtenerCategoria(id));
    }

    private CategoriaProducto obtenerCategoria(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Categoría no encontrada con id: " + id));
    }
}