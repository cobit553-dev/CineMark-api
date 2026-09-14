package com.devsv.cinemark_api.mappers;

import com.devsv.cinemark_api.dto.SalaDTO;
import com.devsv.cinemark_api.entities.Sala;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface SalaMapper {

    SalaDTO toDTO(Sala entity);

    Sala toEntity(SalaDTO dto);

    List<SalaDTO> toDtoList(List<Sala> entities);
}