package com.devsv.cinemark_api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReporteInventarioDTO {

    private Long inventarioId;

    private Long productoId;

    private String productoNombre;

    private Integer stockActual;

    private Integer stockMinimo;
}