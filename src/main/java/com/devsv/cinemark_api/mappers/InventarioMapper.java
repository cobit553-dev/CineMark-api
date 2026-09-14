package com.devsv.cinemark_api.mappers;

import com.devsv.cinemark_api.dto.InventarioDTO;
import com.devsv.cinemark_api.entities.Inventario;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface InventarioMapper {

    @Mapping(source = "producto.id", target = "productoId")
    @Mapping(source = "producto.nombre", target = "productoNombre")
    InventarioDTO toDTO(Inventario entity);

    @Mapping(target = "producto", ignore = true)
    Inventario toEntity(InventarioDTO dto);

    List<InventarioDTO> toDtoList(List<Inventario> entities);
}