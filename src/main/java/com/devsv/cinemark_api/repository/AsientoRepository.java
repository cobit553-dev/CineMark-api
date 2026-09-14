package com.devsv.cinemark_api.repository;

import com.devsv.cinemark_api.entities.Asiento;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AsientoRepository extends JpaRepository<Asiento, Long> {

    List<Asiento> findBySalaId(Long salaId);

    Optional<Asiento> findBySalaIdAndFilaIgnoreCaseAndNumero(Long salaId, String fila, Integer numero);

    boolean existsBySalaIdAndFilaIgnoreCaseAndNumero(Long salaId, String fila, Integer numero);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<Asiento> findWithLockById(Long id);
}