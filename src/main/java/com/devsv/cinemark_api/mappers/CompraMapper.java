package com.devsv.cinemark_api.mappers;

import com.devsv.cinemark_api.dto.CompraDTO;
import com.devsv.cinemark_api.entities.Compra;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring", uses = DetalleCompraMapper.class, unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface CompraMapper {

    @Mapping(source = "usuario.id", target = "usuarioId")
    @Mapping(expression = "java(entity.getUsuario().getNombre() + \" \" + entity.getUsuario().getApellido())", target = "usuarioNombre")
    @Mapping(source = "promocion.id", target = "promocionId")
    @Mapping(source = "promocion.nombre", target = "promocionNombre")
    CompraDTO toDTO(Compra entity);

    @Mapping(target = "usuario", ignore = true)
    @Mapping(target = "promocion", ignore = true)
    Compra toEntity(CompraDTO dto);

    List<CompraDTO> toDtoList(List<Compra> entities);
}