package com.devsv.cinemark_api.mappers;

import com.devsv.cinemark_api.dto.DetalleCompraDTO;
import com.devsv.cinemark_api.entities.DetalleCompra;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface DetalleCompraMapper {

    @Mapping(source = "producto.id", target = "productoId")
    @Mapping(source = "producto.nombre", target = "productoNombre")
    DetalleCompraDTO toDTO(DetalleCompra entity);

    @Mapping(target = "compra", ignore = true)
    @Mapping(target = "producto", ignore = true)
    DetalleCompra toEntity(DetalleCompraDTO dto);

    List<DetalleCompraDTO> toDtoList(List<DetalleCompra> entities);
}