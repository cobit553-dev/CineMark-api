package com.devsv.cinemark_api.entities;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "pelicula_generos", uniqueConstraints = {@UniqueConstraint(name = "uk_pelicula_genero", columnNames = {"pelicula_id", "genero_id"})})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PeliculaGenero {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "pelicula_id", nullable = false)
    private Pelicula pelicula;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "genero_id", nullable = false)
    private Genero genero;
}