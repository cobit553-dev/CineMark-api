package com.devsv.cinemark_api.interfaces;

import com.devsv.cinemark_api.dto.RolDTO;

import java.util.List;

public interface IRolService {

    List<RolDTO> findAll();

    RolDTO findById(Long id);

    RolDTO create(RolDTO dto);

    RolDTO update(Long id, RolDTO dto);

    void delete(Long id);
}