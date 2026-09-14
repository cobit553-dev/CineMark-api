package com.devsv.cinemark_api.interfaces;

import com.devsv.cinemark_api.dto.HistorialClienteDTO;

public interface IHistorialClienteService {

    HistorialClienteDTO obtenerHistorial(String emailUsuario);
}