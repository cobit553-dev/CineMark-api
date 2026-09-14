package com.devsv.cinemark_api.dto;

import jakarta.validation.constraints.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GenerarAsientosRequestDTO {

    @NotNull(message = "La sala es obligatoria")
    private Long salaId;

    @NotBlank(message = "La fila inicial es obligatoria")
    @Pattern(regexp = "^[A-Za-z]$", message = "La fila inicial debe ser una letra entre A y Z")
    private String filaInicial;

    @NotBlank(message = "La fila final es obligatoria")
    @Pattern(regexp = "^[A-Za-z]$", message = "La fila final debe ser una letra entre A y Z")
    private String filaFinal;

    @NotNull(message = "La cantidad de asientos por fila es obligatoria")
    @Min(value = 1, message = "Debe existir al menos un asiento por fila")
    @Max(value = 50, message = "No se permiten más de 50 asientos por fila")
    private Integer cantidadPorFila;

    @NotBlank(message = "El tipo de asiento es obligatorio")
    private String tipo;

    private Boolean estado;
}