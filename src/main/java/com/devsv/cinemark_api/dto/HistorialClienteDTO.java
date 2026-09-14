package com.devsv.cinemark_api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HistorialClienteDTO {

    private Long usuarioId;

    private String usuarioNombre;

    private List<CompraDTO> compras;

    private List<PagoDTO> pagos;

    private List<BoletoDTO> boletos;
}