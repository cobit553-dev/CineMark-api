package com.devsv.cinemark_api.interfaces;

import com.devsv.cinemark_api.dto.ProductoDTO;
import com.devsv.cinemark_api.dto.ProductoRequestDTO;

import java.util.List;

public interface IProductoService {

    List<ProductoDTO> findAll();

    ProductoDTO findById(Long id);

    List<ProductoDTO> findByCategoria(Long categoriaId);

    ProductoDTO create(ProductoRequestDTO dto);

    ProductoDTO update(Long id, ProductoRequestDTO dto);

    void delete(Long id);
}