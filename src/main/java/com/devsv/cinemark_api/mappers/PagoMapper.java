package com.devsv.cinemark_api.mappers;

import com.devsv.cinemark_api.dto.PagoDTO;
import com.devsv.cinemark_api.entities.Pago;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface PagoMapper {

    @Mapping(source = "compra.id", target = "compraId")
    @Mapping(source = "compra.codigoCompra", target = "codigoCompra")
    PagoDTO toDTO(Pago entity);

    @Mapping(target = "compra", ignore = true)
    Pago toEntity(PagoDTO dto);

    List<PagoDTO> toDtoList(List<Pago> entities);
}