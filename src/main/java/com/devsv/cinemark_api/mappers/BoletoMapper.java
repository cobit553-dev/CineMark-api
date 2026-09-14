package com.devsv.cinemark_api.mappers;

import com.devsv.cinemark_api.dto.BoletoDTO;
import com.devsv.cinemark_api.entities.Boleto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface BoletoMapper {

    @Mapping(source = "compra.id", target = "compraId")
    @Mapping(source = "funcion.id", target = "funcionId")
    @Mapping(source = "funcion.pelicula.titulo", target = "peliculaTitulo")
    @Mapping(source = "funcion.fecha", target = "fechaFuncion")
    @Mapping(source = "funcion.horaInicio", target = "horaInicio")
    @Mapping(source = "asiento.id", target = "asientoId")
    @Mapping(source = "asiento.sala.nombre", target = "salaNombre")
    @Mapping(source = "asiento.fila", target = "fila")
    @Mapping(source = "asiento.numero", target = "numero")
    @Mapping(source = "asiento.tipo", target = "tipoAsiento")
    @Mapping(source = "fechaReserva", target = "fechaReserva")
    @Mapping(source = "fechaExpiracion", target = "fechaExpiracion")
    @Mapping(target = "segundosRestantes", expression = "java(calcularSegundosRestantes(entity))")
    BoletoDTO toDTO(Boleto entity);

    List<BoletoDTO> toDtoList(List<Boleto> entities);

    default Long calcularSegundosRestantes(Boleto boleto) {

        if (boleto.getFechaExpiracion() == null) {
            return 0L;
        }

        if (boleto.getEstado() == null || !boleto.getEstado().name().equals("RESERVADO")) {
            return 0L;
        }

        LocalDateTime ahora = LocalDateTime.now();

        if (ahora.isAfter(boleto.getFechaExpiracion())) {
            return 0L;
        }

        return Duration.between(ahora, boleto.getFechaExpiracion()).getSeconds();
    }
}