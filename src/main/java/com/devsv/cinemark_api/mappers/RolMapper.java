package com.devsv.cinemark_api.mappers;

import com.devsv.cinemark_api.dto.RolDTO;
import com.devsv.cinemark_api.entities.Rol;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface RolMapper {

    RolDTO toDTO(Rol entity);

    Rol toEntity(RolDTO dto);

    List<RolDTO> toDtoList(List<Rol> entities);
}