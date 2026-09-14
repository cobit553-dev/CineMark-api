package com.devsv.cinemark_api.mappers;

import com.devsv.cinemark_api.dto.CategoriaProductoDTO;
import com.devsv.cinemark_api.entities.CategoriaProducto;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface CategoriaProductoMapper {

    CategoriaProductoDTO toDTO(CategoriaProducto entity);

    CategoriaProducto toEntity(CategoriaProductoDTO dto);

    List<CategoriaProductoDTO> toDtoList(List<CategoriaProducto> entities);
}