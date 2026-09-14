package com.devsv.cinemark_api.interfaces;

import com.devsv.cinemark_api.dto.LimpiezaSalaDTO;
import com.devsv.cinemark_api.enums.EstadoLimpieza;

import java.util.List;

public interface ILimpiezaSalaService {

    List<LimpiezaSalaDTO> findAll();

    LimpiezaSalaDTO findById(Long id);

    LimpiezaSalaDTO create(LimpiezaSalaDTO limpiezaSalaDTO);

    LimpiezaSalaDTO update(Long id, LimpiezaSalaDTO limpiezaSalaDTO);

    List<LimpiezaSalaDTO> findBySala(Long salaId);

    List<LimpiezaSalaDTO> findByEstado(EstadoLimpieza estado);
}