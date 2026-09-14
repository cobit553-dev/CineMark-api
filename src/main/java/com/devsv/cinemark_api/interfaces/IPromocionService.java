package com.devsv.cinemark_api.interfaces;

import com.devsv.cinemark_api.dto.PromocionDTO;

import java.util.List;

public interface IPromocionService {

    List<PromocionDTO> findAll();

    PromocionDTO findById(Long id);

    List<PromocionDTO> findVigentes();

    PromocionDTO create(PromocionDTO promocionDTO);

    PromocionDTO update(Long id, PromocionDTO promocionDTO);
}