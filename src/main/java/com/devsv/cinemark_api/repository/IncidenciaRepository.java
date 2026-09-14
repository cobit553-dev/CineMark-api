package com.devsv.cinemark_api.repository;

import com.devsv.cinemark_api.entities.Incidencia;
import com.devsv.cinemark_api.enums.EstadoIncidencia;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IncidenciaRepository extends JpaRepository<Incidencia, Long> {

    List<Incidencia> findBySalaId(Long salaId);

    List<Incidencia> findByEstado(EstadoIncidencia estado);

    List<Incidencia> findAllByOrderByFechaDesc();
}