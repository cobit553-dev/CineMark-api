package com.devsv.cinemark_api.mappers;

import com.devsv.cinemark_api.dto.LimpiezaSalaDTO;
import com.devsv.cinemark_api.entities.LimpiezaSala;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface LimpiezaSalaMapper {

    @Mapping(source = "sala.id", target = "salaId")
    @Mapping(source = "sala.nombre", target = "salaNombre")
    LimpiezaSalaDTO toDTO(LimpiezaSala limpiezaSala);
}