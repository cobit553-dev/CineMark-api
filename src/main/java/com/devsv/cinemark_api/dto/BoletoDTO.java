package com.devsv.cinemark_api.dto;

import com.devsv.cinemark_api.enums.EstadoBoleto;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BoletoDTO {

    private Long id;

    private Long compraId;

    private Long funcionId;

    private String peliculaTitulo;

    private LocalDate fechaFuncion;

    private LocalTime horaInicio;

    private Long asientoId;

    private String salaNombre;

    private String fila;

    private Integer numero;

    private String tipoAsiento;

    private BigDecimal precio;

    private String codigoQr;

    private EstadoBoleto estado;

    private LocalDateTime fechaReserva;

    private LocalDateTime fechaExpiracion;

    private Long segundosRestantes;
}