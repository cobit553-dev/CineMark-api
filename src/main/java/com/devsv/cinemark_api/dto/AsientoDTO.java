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
public class AsientoDTO {

    private Long id;

    @NotNull(message = "La sala es obligatoria")
    private Long salaId;

    private String salaNombre;

    @NotBlank(message = "La fila es obligatoria")
    private String fila;

    @NotNull(message = "El número de asiento es obligatorio")
    @Min(value = 1, message = "El número debe ser mayor que cero")
    private Integer numero;

    @NotBlank(message = "El tipo de asiento es obligatorio")
    private String tipo;

    private Boolean estado;
}