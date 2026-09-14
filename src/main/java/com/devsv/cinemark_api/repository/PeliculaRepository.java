package com.devsv.cinemark_api.repository;

import com.devsv.cinemark_api.entities.Pelicula;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PeliculaRepository extends JpaRepository<Pelicula, Long> {

    boolean existsByTituloIgnoreCase(String titulo);

    Optional<Pelicula> findByTituloIgnoreCase(String titulo);
}