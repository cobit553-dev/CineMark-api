package com.devsv.cinemark_api.interfaces;

import com.devsv.cinemark_api.dto.PeliculaDTO;
import com.devsv.cinemark_api.dto.PeliculaRequestDTO;

import java.util.List;

public interface IPeliculaService {

    List<PeliculaDTO> findAll();

    PeliculaDTO findById(Long id);

    PeliculaDTO create(PeliculaRequestDTO dto);

    PeliculaDTO update(Long id, PeliculaRequestDTO dto);

    void delete(Long id);
}