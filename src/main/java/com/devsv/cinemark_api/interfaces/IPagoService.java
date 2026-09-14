package com.devsv.cinemark_api.interfaces;

import com.devsv.cinemark_api.dto.PagoDTO;
import com.devsv.cinemark_api.dto.PagoRequestDTO;

import java.util.List;

public interface IPagoService {

    List<PagoDTO> findAll();

    PagoDTO findById(Long id, String emailUsuario);

    PagoDTO findByCompra(Long compraId, String emailUsuario);

    PagoDTO realizarPago(PagoRequestDTO dto, String emailUsuario);
}