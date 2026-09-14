package com.devsv.cinemark_api.mappers;

import com.devsv.cinemark_api.dto.MovimientoInventarioDTO;
import com.devsv.cinemark_api.entities.MovimientoInventario;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface MovimientoInventarioMapper {

    @Mapping(source = "inventario.id", target = "inventarioId")
    @Mapping(source = "inventario.producto.nombre", target = "productoNombre")
    @Mapping(expression = """
            java(
                entity.getUsuario() != null
                    ? entity.getUsuario().getId()
                    : null
            )
            """, target = "usuarioId")
    @Mapping(expression = """
            java(
                entity.getUsuario() != null
                    ? construirNombreUsuario(entity)
                    : "SISTEMA"
            )
            """, target = "usuarioNombre")
    MovimientoInventarioDTO toDTO(MovimientoInventario entity);

    @Mapping(target = "inventario", ignore = true)
    @Mapping(target = "usuario", ignore = true)
    MovimientoInventario toEntity(MovimientoInventarioDTO dto);

    List<MovimientoInventarioDTO> toDtoList(List<MovimientoInventario> entities);

    default String construirNombreUsuario(MovimientoInventario entity) {

        if (entity.getUsuario() == null) {
            return "SISTEMA";
        }

        String nombre = entity.getUsuario().getNombre() != null ? entity.getUsuario().getNombre().trim() : "";

        String apellido = entity.getUsuario().getApellido() != null ? entity.getUsuario().getApellido().trim() : "";

        return (nombre + " " + apellido).trim();
    }
}