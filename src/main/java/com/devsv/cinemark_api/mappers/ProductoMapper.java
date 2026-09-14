package com.devsv.cinemark_api.mappers;

import com.devsv.cinemark_api.dto.ProductoDTO;
import com.devsv.cinemark_api.entities.Producto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ProductoMapper {

    @Mapping(source = "categoria.id", target = "categoriaId")
    @Mapping(source = "categoria.nombre", target = "categoriaNombre")
    ProductoDTO toDTO(Producto entity);

    @Mapping(target = "categoria", ignore = true)
    Producto toEntity(ProductoDTO dto);

    List<ProductoDTO> toDtoList(List<Producto> entities);
}