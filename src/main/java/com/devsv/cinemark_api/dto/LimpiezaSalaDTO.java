package com.devsv.cinemark_api.dto;

import com.devsv.cinemark_api.enums.EstadoLimpieza;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LimpiezaSalaDTO {

    private Long id;

    private Long salaId;

    private String salaNombre;

    private LocalDateTime fechaHora;

    private String responsable;

    private String observaciones;

    private EstadoLimpieza estado;
}