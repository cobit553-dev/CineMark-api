package com.devsv.cinemark_api.interfaces;

import com.devsv.cinemark_api.dto.IncidenciaDTO;
import com.devsv.cinemark_api.enums.EstadoIncidencia;

import java.util.List;

public interface IIncidenciaService {

    List<IncidenciaDTO> findAll();

    IncidenciaDTO findById(Long id);

    IncidenciaDTO create(IncidenciaDTO incidenciaDTO);

    IncidenciaDTO update(Long id, IncidenciaDTO incidenciaDTO);

    List<IncidenciaDTO> findBySala(Long salaId);

    List<IncidenciaDTO> findByEstado(EstadoIncidencia estado);
}