package com.devsv.cinemark_api.repository;

import com.devsv.cinemark_api.dto.PeliculaMasVendidaDTO;
import com.devsv.cinemark_api.dto.ReportePeliculaDTO;
import com.devsv.cinemark_api.entities.Boleto;
import com.devsv.cinemark_api.enums.EstadoBoleto;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface BoletoRepository extends JpaRepository<Boleto, Long> {

    boolean existsByFuncionIdAndAsientoId(Long funcionId, Long asientoId);

    List<Boleto> findByCompraId(Long compraId);

    List<Boleto> findByCompraIdAndEstado(Long compraId, EstadoBoleto estado);

    List<Boleto> findByFuncionId(Long funcionId);

    long countByEstado(EstadoBoleto estado);

    @Query("""
            SELECT new com.devsv.cinemark_api.dto.PeliculaMasVendidaDTO(
                b.funcion.pelicula.id,
                b.funcion.pelicula.titulo,
                COUNT(b),
                COALESCE(SUM(b.precio), 0)
            )
            FROM Boleto b
            WHERE b.estado = :estado
            GROUP BY b.funcion.pelicula.id, b.funcion.pelicula.titulo
            ORDER BY COUNT(b) DESC
            """)
    List<PeliculaMasVendidaDTO> findPeliculasMasVendidas(@Param("estado") EstadoBoleto estado);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    List<Boleto> findByEstadoAndFechaExpiracionBefore(EstadoBoleto estado, LocalDateTime fecha);

    @Query("""
            SELECT new com.devsv.cinemark_api.dto.ReportePeliculaDTO(
                b.funcion.pelicula.id,
                b.funcion.pelicula.titulo,
                COUNT(b),
                COALESCE(SUM(b.precio), 0)
            )
            FROM Boleto b
            WHERE b.estado = :estado
            AND b.compra.fecha >= :inicio
            AND b.compra.fecha < :fin
            GROUP BY
                b.funcion.pelicula.id,
                b.funcion.pelicula.titulo
            ORDER BY COUNT(b) DESC
            """)
    List<ReportePeliculaDTO> findPeliculasVendidasPorRango(@Param("estado") EstadoBoleto estado,
            @Param("inicio") LocalDateTime inicio, @Param("fin") LocalDateTime fin);
}