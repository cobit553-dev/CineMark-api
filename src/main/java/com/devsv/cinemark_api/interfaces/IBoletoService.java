package com.devsv.cinemark_api.interfaces;

import com.devsv.cinemark_api.dto.BoletoDTO;
import com.devsv.cinemark_api.dto.BoletoRequestDTO;

import java.util.List;

public interface IBoletoService {

    BoletoDTO reservar(BoletoRequestDTO dto, String emailUsuario);

    BoletoDTO findById(Long id, String emailUsuario);

    List<BoletoDTO> findByCompra(Long compraId, String emailUsuario);

    List<BoletoDTO> findByFuncion(Long funcionId);
}