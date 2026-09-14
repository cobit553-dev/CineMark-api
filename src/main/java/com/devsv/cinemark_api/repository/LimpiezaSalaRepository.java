package com.devsv.cinemark_api.repository;

import com.devsv.cinemark_api.entities.LimpiezaSala;
import com.devsv.cinemark_api.enums.EstadoLimpieza;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LimpiezaSalaRepository extends JpaRepository<LimpiezaSala, Long> {

    List<LimpiezaSala> findAllByOrderByFechaHoraDesc();

    List<LimpiezaSala> findBySalaIdOrderByFechaHoraDesc(Long salaId);

    List<LimpiezaSala> findByEstadoOrderByFechaHoraDesc(EstadoLimpieza estado);
}