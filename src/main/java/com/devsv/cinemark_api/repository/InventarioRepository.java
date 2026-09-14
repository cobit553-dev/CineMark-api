package com.devsv.cinemark_api.repository;

import com.devsv.cinemark_api.dto.ReporteInventarioDTO;
import com.devsv.cinemark_api.entities.Inventario;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface InventarioRepository extends JpaRepository<Inventario, Long> {

    Optional<Inventario> findByProductoId(Long productoId);

    boolean existsByProductoId(Long productoId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<Inventario> findWithLockByProductoId(Long productoId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<Inventario> findWithLockById(Long id);

    @Query("""
            SELECT COUNT(i)
            FROM Inventario i
            WHERE i.stockActual <= i.stockMinimo
            """)
    long countProductosStockBajo();

    @Query("""
            SELECT new com.devsv.cinemark_api.dto.ReporteInventarioDTO(
                i.id,
                i.producto.id,
                i.producto.nombre,
                i.stockActual,
                i.stockMinimo
            )
            FROM Inventario i
            WHERE i.stockActual <= i.stockMinimo
            ORDER BY i.stockActual ASC
            """)
    List<ReporteInventarioDTO> findProductosConStockBajo();
}