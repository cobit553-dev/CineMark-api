package com.devsv.cinemark_api.mappers;

import com.devsv.cinemark_api.dto.AsientoDTO;
import com.devsv.cinemark_api.entities.Asiento;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface AsientoMapper {

    @Mapping(source = "sala.id", target = "salaId")
    @Mapping(source = "sala.nombre", target = "salaNombre")
    AsientoDTO toDTO(Asiento entity);

    @Mapping(target = "sala", ignore = true)
    Asiento toEntity(AsientoDTO dto);

    List<AsientoDTO> toDtoList(List<Asiento> entities);
}