package com.devsv.cinemark_api.mappers;

import com.devsv.cinemark_api.dto.PromocionDTO;
import com.devsv.cinemark_api.entities.Promocion;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface PromocionMapper {

    PromocionDTO toDTO(Promocion promocion);

    List<PromocionDTO> toDtoList(List<Promocion> promociones);
}
