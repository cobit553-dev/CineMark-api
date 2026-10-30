package com.devsv.cinemark_api.mappers;

import com.devsv.cinemark_api.dto.PeliculaDTO;
import com.devsv.cinemark_api.entities.Pelicula;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface PeliculaMapper {

    @Mapping(target = "generosIds", ignore = true)
    @Mapping(target = "generosNombres", ignore = true)
    PeliculaDTO toDTO(Pelicula entity);

    List<PeliculaDTO> toDtoList(List<Pelicula> entities);
}