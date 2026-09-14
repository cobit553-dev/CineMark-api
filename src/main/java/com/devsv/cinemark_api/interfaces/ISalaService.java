package com.devsv.cinemark_api.interfaces;

import com.devsv.cinemark_api.dto.SalaDTO;

import java.util.List;

public interface ISalaService {

    List<SalaDTO> findAll();

    SalaDTO findById(Long id);

    SalaDTO create(SalaDTO dto);

    SalaDTO update(Long id, SalaDTO dto);

    void delete(Long id);
}