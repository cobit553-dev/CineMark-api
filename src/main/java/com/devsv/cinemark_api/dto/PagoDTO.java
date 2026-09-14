package com.devsv.cinemark_api.dto;

import com.devsv.cinemark_api.enums.EstadoPago;
import com.devsv.cinemark_api.enums.MetodoPago;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PagoDTO {

    private Long id;

    private Long compraId;

    private String codigoCompra;

    private BigDecimal monto;

    private MetodoPago metodoPago;

    private String referencia;

    private LocalDateTime fechaPago;

    private EstadoPago estado;
}