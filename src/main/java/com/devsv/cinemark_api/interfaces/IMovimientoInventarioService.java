package com.devsv.cinemark_api.interfaces;

import com.devsv.cinemark_api.dto.MovimientoInventarioDTO;

import java.util.List;

public interface IMovimientoInventarioService {

    List<MovimientoInventarioDTO> findAll();

    MovimientoInventarioDTO findById(Long id);

    List<MovimientoInventarioDTO> findByInventario(Long inventarioId);

    MovimientoInventarioDTO create(MovimientoInventarioDTO dto, String emailUsuario);

    MovimientoInventarioDTO createSistema(MovimientoInventarioDTO dto);
}