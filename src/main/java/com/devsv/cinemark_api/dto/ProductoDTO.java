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
public class ProductoDTO {

    private Long id;

    private Long categoriaId;

    private String categoriaNombre;

    private String nombre;

    private String descripcion;

    private BigDecimal precio;

    private String imagen;

    private Boolean estado;
}