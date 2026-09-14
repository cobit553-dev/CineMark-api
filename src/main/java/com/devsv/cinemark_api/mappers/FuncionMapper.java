package com.devsv.cinemark_api.mappers;

import com.devsv.cinemark_api.dto.FuncionDTO;
import com.devsv.cinemark_api.entities.Funcion;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface FuncionMapper {

    @Mapping(source = "pelicula.id", target = "peliculaId")
    @Mapping(source = "pelicula.titulo", target = "peliculaTitulo")
    @Mapping(source = "sala.id", target = "salaId")
    @Mapping(source = "sala.nombre", target = "salaNombre")
    FuncionDTO toDTO(Funcion entity);

    @Mapping(target = "pelicula", ignore = true)
    @Mapping(target = "sala", ignore = true)
    Funcion toEntity(FuncionDTO dto);

    List<FuncionDTO> toDtoList(List<Funcion> entities);
}