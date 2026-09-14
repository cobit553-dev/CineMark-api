package com.devsv.cinemark_api.dto;

import com.devsv.cinemark_api.enums.NombreRol;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RolDTO {

    private Long id;

    @NotNull(message = "El nombre del rol es obligatorio")
    private NombreRol nombre;

    @Size(max = 255, message = "La descripción no puede superar los 255 caracteres")
    private String descripcion;

    private Boolean estado;
}