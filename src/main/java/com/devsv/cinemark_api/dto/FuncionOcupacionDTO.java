package com.devsv.cinemark_api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FuncionOcupacionDTO {

    private Long funcionId;

    private String peliculaTitulo;

    private String salaNombre;

    private LocalDate fecha;

    private LocalTime horaInicio;

    private Long boletosVendidos;

    private Integer capacidadSala;

    private BigDecimal porcentajeOcupacion;
}