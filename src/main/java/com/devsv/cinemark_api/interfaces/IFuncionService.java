package com.devsv.cinemark_api.interfaces;

import com.devsv.cinemark_api.dto.FuncionDTO;

import java.time.LocalDate;
import java.util.List;

public interface IFuncionService {

    List<FuncionDTO> findAll();

    FuncionDTO findById(Long id);

    List<FuncionDTO> findByPelicula(Long peliculaId);

    List<FuncionDTO> findBySala(Long salaId);

    List<FuncionDTO> findByFecha(LocalDate fecha);

    FuncionDTO create(FuncionDTO dto);

    FuncionDTO update(Long id, FuncionDTO dto);

    void delete(Long id);
}