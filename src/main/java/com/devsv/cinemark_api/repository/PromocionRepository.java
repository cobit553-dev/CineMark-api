package com.devsv.cinemark_api.repository;

import com.devsv.cinemark_api.entities.Promocion;
import com.devsv.cinemark_api.enums.EstadoPromocion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface PromocionRepository extends JpaRepository<Promocion, Long> {

    List<Promocion> findAllByOrderByFechaInicioDesc();

    List<Promocion> findByEstadoOrderByFechaInicioDesc(EstadoPromocion estado);

    List<Promocion> findByEstadoAndFechaInicioLessThanEqualAndFechaFinGreaterThanEqual(EstadoPromocion estado,
            LocalDate fechaInicio, LocalDate fechaFin);
}