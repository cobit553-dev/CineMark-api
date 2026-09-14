package com.devsv.cinemark_api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PeliculaMasVendidaDTO {

    private Long peliculaId;

    private String peliculaTitulo;

    private Long boletosVendidos;

    private BigDecimal totalGenerado;
}