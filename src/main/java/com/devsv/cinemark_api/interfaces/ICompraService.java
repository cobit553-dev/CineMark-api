package com.devsv.cinemark_api.interfaces;

import com.devsv.cinemark_api.dto.CompraDTO;
import com.devsv.cinemark_api.dto.CompraProductosRequestDTO;
import com.devsv.cinemark_api.dto.CompraRequestDTO;

import java.util.List;

public interface ICompraService {

    List<CompraDTO> findAll();

    CompraDTO findById(Long id, String emailUsuario);

    List<CompraDTO> findMisCompras(String email);

    CompraDTO create(CompraRequestDTO dto, String emailUsuario);

    CompraDTO actualizarProductos(Long id, CompraProductosRequestDTO dto, String emailUsuario);
}