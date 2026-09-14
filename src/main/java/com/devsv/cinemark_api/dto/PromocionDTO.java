package com.devsv.cinemark_api.dto;

import com.devsv.cinemark_api.enums.EstadoPromocion;
import com.devsv.cinemark_api.enums.TipoAplicacionPromocion;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PromocionDTO {

    private Long id;

    private String nombre;

    private String descripcion;

    private BigDecimal porcentajeDescuento;

    private LocalDate fechaInicio;

    private LocalDate fechaFin;

    private DayOfWeek diaSemana;

    private TipoAplicacionPromocion tipoAplicacion;

    private EstadoPromocion estado;
}