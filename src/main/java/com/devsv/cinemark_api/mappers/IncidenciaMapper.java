package com.devsv.cinemark_api.mappers;

import com.devsv.cinemark_api.dto.IncidenciaDTO;
import com.devsv.cinemark_api.entities.Incidencia;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface IncidenciaMapper {

    @Mapping(source = "sala.id", target = "salaId")
    @Mapping(source = "sala.nombre", target = "salaNombre")
    IncidenciaDTO toDTO(Incidencia incidencia);
}