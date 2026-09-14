package com.devsv.cinemark_api.interfaces;

import com.devsv.cinemark_api.dto.ComprobanteDTO;

public interface IComprobanteService {

    ComprobanteDTO obtenerComprobante(Long compraId, String emailUsuario);
}