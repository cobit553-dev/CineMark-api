package com.devsv.cinemark_api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReporteVentasDTO {

    private LocalDate fechaInicio;

    private LocalDate fechaFin;

    private Long cantidadCompras;

    private BigDecimal totalVentas;
}