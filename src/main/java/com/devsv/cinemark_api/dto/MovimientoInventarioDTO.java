package com.devsv.cinemark_api.dto;

import com.devsv.cinemark_api.enums.TipoMovimientoInventario;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MovimientoInventarioDTO {

    private Long id;

    @NotNull(message = "El inventario es obligatorio")
    private Long inventarioId;

    private String productoNombre;

    private Long usuarioId;

    private String usuarioNombre;

    @NotNull(message = "El tipo de movimiento es obligatorio")
    private TipoMovimientoInventario tipo;

    @NotNull(message = "La cantidad es obligatoria")
    @Min(value = 0, message = "La cantidad no puede ser negativa")
    private Integer cantidad;

    @NotBlank(message = "El motivo es obligatorio")
    @Size(max = 255)
    private String motivo;

    private LocalDateTime fecha;
}