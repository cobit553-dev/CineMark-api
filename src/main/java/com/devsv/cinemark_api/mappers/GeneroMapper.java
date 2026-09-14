package com.devsv.cinemark_api.mappers;

import com.devsv.cinemark_api.dto.GeneroDTO;
import com.devsv.cinemark_api.entities.Genero;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface GeneroMapper {

    GeneroDTO toDTO(Genero entity);

    Genero toEntity(GeneroDTO dto);

    List<GeneroDTO> toDtoList(List<Genero> entities);
}