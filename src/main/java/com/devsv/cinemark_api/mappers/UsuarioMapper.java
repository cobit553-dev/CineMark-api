package com.devsv.cinemark_api.mappers;

import com.devsv.cinemark_api.dto.UsuarioDTO;
import com.devsv.cinemark_api.entities.Usuario;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface UsuarioMapper {

    @Mapping(source = "rol.id", target = "rolId")
    @Mapping(source = "rol.nombre", target = "rolNombre")
    UsuarioDTO toDTO(Usuario entity);

    @Mapping(target = "rol", ignore = true)
    Usuario toEntity(UsuarioDTO dto);

    List<UsuarioDTO> toDtoList(List<Usuario> entities);
}