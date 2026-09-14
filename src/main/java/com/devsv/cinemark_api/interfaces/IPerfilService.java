package com.devsv.cinemark_api.interfaces;

import com.devsv.cinemark_api.dto.CambiarPasswordDTO;
import com.devsv.cinemark_api.dto.PerfilDTO;
import com.devsv.cinemark_api.dto.PerfilUpdateDTO;

public interface IPerfilService {

    PerfilDTO obtenerPerfil(String emailUsuario);

    PerfilDTO actualizarPerfil(PerfilUpdateDTO dto, String emailUsuario);

    void cambiarPassword(CambiarPasswordDTO dto, String emailUsuario);
}