package com.devsv.cinemark_api.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SalaDTO {

    private Long id;

    @NotBlank(message = "El nombre de la sala es obligatorio")
    private String nombre;

    @NotNull(message = "La capacidad es obligatoria")
    @Min(value = 1, message = "La capacidad debe ser mayor que cero")
    private Integer capacidad;

    @NotBlank(message = "El tipo de sala es obligatorio")
    private String tipo;

    @NotBlank(message = "El estado de la sala es obligatorio")
    private String estado;
}