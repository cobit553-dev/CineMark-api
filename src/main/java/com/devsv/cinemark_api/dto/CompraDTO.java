package com.devsv.cinemark_api.dto;

import com.devsv.cinemark_api.enums.EstadoCompra;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CompraDTO {

    private Long id;

    private Long usuarioId;

    private String usuarioNombre;

    private LocalDateTime fecha;

    private BigDecimal subtotal;

    private BigDecimal descuento;

    private BigDecimal total;

    private String codigoCompra;

    private Long promocionId;

    private String promocionNombre;

    private EstadoCompra estado;

    private List<DetalleCompraDTO> detalles;
}