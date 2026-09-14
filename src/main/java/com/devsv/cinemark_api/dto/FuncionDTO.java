package com.devsv.cinemark_api.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FuncionDTO {

    private Long id;

    @NotNull(message = "La película es obligatoria")
    private Long peliculaId;

    private String peliculaTitulo;

    @NotNull(message = "La sala es obligatoria")
    private Long salaId;

    private String salaNombre;

    @NotNull(message = "La fecha es obligatoria")
    private LocalDate fecha;

    @NotNull(message = "La hora de inicio es obligatoria")
    private LocalTime horaInicio;

    @NotNull(message = "La hora de fin es obligatoria")
    private LocalTime horaFin;

    @NotNull(message = "El precio es obligatorio")
    @DecimalMin(value = "0.01", message = "El precio debe ser mayor que cero")
    private BigDecimal precio;

    @NotBlank(message = "El tipo de función es obligatorio")
    private String tipo;

    @NotBlank(message = "El estado es obligatorio")
    private String estado;
}