package com.devsv.cinemark_api.repository;

import com.devsv.cinemark_api.entities.Pago;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PagoRepository extends JpaRepository<Pago, Long> {

    Optional<Pago> findByCompraId(Long compraId);

    boolean existsByCompraId(Long compraId);
}