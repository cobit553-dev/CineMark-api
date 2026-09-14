package com.devsv.cinemark_api.repository;

import com.devsv.cinemark_api.entities.PeliculaGenero;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PeliculaGeneroRepository extends JpaRepository<PeliculaGenero, Long> {

    List<PeliculaGenero> findByPeliculaId(Long peliculaId);

    void deleteByPeliculaId(Long peliculaId);
}