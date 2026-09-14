package com.devsv.cinemark_api.dto;

import lombok.*;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PeliculaDTO {

    private Long id;

    private String titulo;

    private String clasificacion;

    private Integer duracion;

    private String sinopsis;

    private String imagen;

    private String trailerUrl;

    private LocalDate fechaEstreno;

    private LocalDate fechaFinExhibicion;

    private Boolean estado;

    private List<Long> generosIds;

    private List<String> generosNombres;
}