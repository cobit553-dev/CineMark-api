package com.devsv.cinemark_api.entities;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "peliculas")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
// OSWALDO - ARQUITECTURA: entidad JPA mapeada a tabla 'peliculas', base del modelo de dominio
public class Pelicula {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 200)
    private String titulo;

    @Column(nullable = false, length = 20)
    private String clasificacion;

    @Column(nullable = false)
    private Integer duracion;

    @Column(columnDefinition = "TEXT")
    private String sinopsis;

    @Column(length = 255)
    private String imagen;

    @Column(name = "trailer_url", length = 500)
    private String trailerUrl;

    @Column(name = "fecha_estreno", nullable = false)
    private LocalDate fechaEstreno;

    @Column(name = "fecha_fin_exhibicion", nullable = false)
    private LocalDate fechaFinExhibicion;

    @Column(nullable = false)
    private Boolean estado = true;
}