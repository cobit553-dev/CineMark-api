package com.devsv.cinemark_api.dto;

import com.devsv.cinemark_api.enums.EstadoCompra;
import com.devsv.cinemark_api.enums.EstadoPago;
import com.devsv.cinemark_api.enums.MetodoPago;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ComprobanteDTO {

    private Long compraId;

    private String codigoCompra;

    private LocalDateTime fechaCompra;

    private Long usuarioId;

    private String usuarioNombre;

    private EstadoCompra estadoCompra;

    private BigDecimal subtotal;

    private BigDecimal descuento;

    private BigDecimal total;

    private Long promocionId;

    private String promocionNombre;

    private BigDecimal porcentajeDescuento;

    private List<DetalleCompraDTO> productos;

    private List<BoletoDTO> boletos;

    private Long pagoId;

    private BigDecimal montoPagado;

    private MetodoPago metodoPago;

    private String referenciaPago;

    private LocalDateTime fechaPago;

    private EstadoPago estadoPago;
}