package com.devsv.cinemark_api.interfaces;

import com.devsv.cinemark_api.dto.CategoriaProductoDTO;

import java.util.List;

public interface ICategoriaProductoService {

    List<CategoriaProductoDTO> findAll();

    CategoriaProductoDTO findById(Long id);

    CategoriaProductoDTO create(CategoriaProductoDTO dto);

    CategoriaProductoDTO update(Long id, CategoriaProductoDTO dto);

    void delete(Long id);
}