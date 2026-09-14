package com.devsv.cinemark_api.interfaces;

import com.devsv.cinemark_api.dto.AsientoDTO;
import com.devsv.cinemark_api.dto.AsientoDisponibilidadDTO;
import com.devsv.cinemark_api.dto.GenerarAsientosRequestDTO;

import java.util.List;

public interface IAsientoService {

    List<AsientoDTO> findAll();

    AsientoDTO findById(Long id);

    List<AsientoDTO> findBySala(Long salaId);

    List<AsientoDisponibilidadDTO> findDisponibilidadByFuncion(Long funcionId);

    AsientoDTO create(AsientoDTO dto);

    List<AsientoDTO> generar(GenerarAsientosRequestDTO dto);

    AsientoDTO update(Long id, AsientoDTO dto);

    void delete(Long id);
}