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
public class DashboardAdminDTO {

    private Long totalUsuarios;

    private Long totalPeliculas;

    private Long totalFunciones;

    private Long totalProductos;

    private Long totalCompras;

    private Long comprasPendientes;

    private Long comprasPagadas;

    private Long comprasCanceladas;

    private Long boletosVendidos;

    private Long productosStockBajo;

    private BigDecimal ingresosTotales;
}