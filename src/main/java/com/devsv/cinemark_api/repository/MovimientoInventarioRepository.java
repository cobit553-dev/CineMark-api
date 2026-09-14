package com.devsv.cinemark_api.repository;

import com.devsv.cinemark_api.entities.MovimientoInventario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MovimientoInventarioRepository extends JpaRepository<MovimientoInventario, Long> {

    List<MovimientoInventario> findByInventarioId(Long inventarioId);

    List<MovimientoInventario> findByUsuarioId(Long usuarioId);

    List<MovimientoInventario> findAllByOrderByFechaDesc();
}