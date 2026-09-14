package com.devsv.cinemark_api.interfaces;

import com.devsv.cinemark_api.dto.PerfilClienteUpdateDTO;
import com.devsv.cinemark_api.dto.UsuarioDTO;

import java.util.List;

public interface IUsuarioService {

    List<UsuarioDTO> findAll();

    UsuarioDTO findById(Long id);

    UsuarioDTO findPerfil(String email);

    UsuarioDTO create(UsuarioDTO dto);

    UsuarioDTO update(Long id, UsuarioDTO dto);

    UsuarioDTO updatePerfil(String email, PerfilClienteUpdateDTO dto);

    void delete(Long id);
}