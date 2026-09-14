package com.devsv.cinemark_api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AsientoDisponibilidadDTO {

    private Long asientoId;
    private Long salaId;
    private String salaNombre;
    private String fila;
    private Integer numero;
    private String tipo;
    private Boolean estado;
    private Boolean disponible;
}