package com.devsv.cinemark_api.interfaces;

import com.devsv.cinemark_api.dto.GeneroDTO;

import java.util.List;

public interface IGeneroService {

    List<GeneroDTO> findAll();

    GeneroDTO findById(Long id);

    GeneroDTO create(GeneroDTO generoDTO);

    GeneroDTO update(Long id, GeneroDTO generoDTO);

    void delete(Long id);
}