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
public class ProductoMasVendidoDTO {

    private Long productoId;

    private String productoNombre;

    private Long cantidadVendida;

    private BigDecimal totalGenerado;
}