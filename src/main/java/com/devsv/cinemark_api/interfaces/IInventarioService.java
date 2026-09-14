package com.devsv.cinemark_api.interfaces;

import com.devsv.cinemark_api.dto.InventarioDTO;

import java.util.List;

public interface IInventarioService {

    List<InventarioDTO> findAll();

    InventarioDTO findById(Long id);

    InventarioDTO findByProducto(Long productoId);

    InventarioDTO create(InventarioDTO dto);

    InventarioDTO update(Long id, InventarioDTO dto);

    void delete(Long id);
}