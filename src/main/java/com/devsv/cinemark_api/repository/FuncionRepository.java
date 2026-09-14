package com.devsv.cinemark_api.repository;

import com.devsv.cinemark_api.entities.Funcion;
import com.devsv.cinemark_api.enums.EstadoBoleto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface FuncionRepository extends JpaRepository<Funcion, Long> {

    List<Funcion> findByPeliculaId(Long peliculaId);

    List<Funcion> findBySalaId(Long salaId);

    List<Funcion> findByFecha(LocalDate fecha);

    List<Funcion> findBySalaIdAndFecha(Long salaId, LocalDate fecha);

    @Query("""
            SELECT
                f.id,
                f.pelicula.titulo,
                f.sala.nombre,
                f.fecha,
                f.horaInicio,
                COUNT(b.id),
                f.sala.capacidad
            FROM Funcion f
            LEFT JOIN Boleto b
                ON b.funcion.id = f.id
                AND b.estado = :estado
            GROUP BY
                f.id,
                f.pelicula.titulo,
                f.sala.nombre,
                f.fecha,
                f.horaInicio,
                f.sala.capacidad
            ORDER BY COUNT(b.id) DESC
            """)
    List<Object[]> findDatosOcupacionFunciones(@Param("estado") EstadoBoleto estado);
}