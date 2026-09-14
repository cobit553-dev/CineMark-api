package com.devsv.cinemark_api.dto;

import com.devsv.cinemark_api.enums.EstadoIncidencia;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class IncidenciaDTO {

    private Long id;

    private Long salaId;

    private String salaNombre;

    private String titulo;

    private String descripcion;

    private LocalDateTime fecha;

    private EstadoIncidencia estado;
}