package com.devsv.cinemark_api.repository;

import com.devsv.cinemark_api.dto.VentaPorFechaDTO;
import com.devsv.cinemark_api.entities.Compra;
import com.devsv.cinemark_api.enums.EstadoCompra;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface CompraRepository extends JpaRepository<Compra, Long> {

    Optional<Compra> findByCodigoCompra(String codigoCompra);

    List<Compra> findByUsuarioIdOrderByFechaDesc(Long usuarioId);

    List<Compra> findAllByOrderByFechaDesc();

    long countByEstado(EstadoCompra estado);

    long countByEstadoAndFechaGreaterThanEqualAndFechaLessThan(EstadoCompra estado, LocalDateTime inicio,
            LocalDateTime fin);

    @Query("""
            SELECT COALESCE(SUM(c.total), 0)
            FROM Compra c
            WHERE c.estado = :estado
            """)
    BigDecimal sumTotalByEstado(@Param("estado") EstadoCompra estado);

    @Query("""
            SELECT COALESCE(SUM(c.total), 0)
            FROM Compra c
            WHERE c.estado = :estado
            AND c.fecha >= :inicio
            AND c.fecha < :fin
            """)
    BigDecimal sumTotalByEstadoAndFecha(@Param("estado") EstadoCompra estado, @Param("inicio") LocalDateTime inicio,
            @Param("fin") LocalDateTime fin);

    @Query("""
            SELECT new com.devsv.cinemark_api.dto.VentaPorFechaDTO(
                CAST(c.fecha AS LocalDate),
                COUNT(c),
                COALESCE(SUM(c.total), 0)
            )
            FROM Compra c
            WHERE c.estado = :estado
            GROUP BY CAST(c.fecha AS LocalDate)
            ORDER BY CAST(c.fecha AS LocalDate)
            """)
    List<VentaPorFechaDTO> findVentasPorFecha(@Param("estado") EstadoCompra estado);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("""
            SELECT c
            FROM Compra c
            WHERE c.id = :id
            """)
    Optional<Compra> findWithLockById(@Param("id") Long id);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    List<Compra> findByEstadoAndFechaExpiracionBefore(EstadoCompra estado, LocalDateTime fecha);
}