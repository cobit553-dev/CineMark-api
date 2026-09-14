package com.devsv.cinemark_api.repository;

import com.devsv.cinemark_api.dto.ProductoMasVendidoDTO;
import com.devsv.cinemark_api.dto.ReporteProductoDTO;
import com.devsv.cinemark_api.entities.DetalleCompra;
import com.devsv.cinemark_api.enums.EstadoCompra;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface DetalleCompraRepository extends JpaRepository<DetalleCompra, Long> {

    List<DetalleCompra> findByCompraId(Long compraId);

    @Query("""
            SELECT new com.devsv.cinemark_api.dto.ProductoMasVendidoDTO(
                d.producto.id,
                d.producto.nombre,
                SUM(d.cantidad),
                SUM(d.subtotal)
            )
            FROM DetalleCompra d
            WHERE d.compra.estado = :estado
            GROUP BY d.producto.id, d.producto.nombre
            ORDER BY SUM(d.cantidad) DESC
            """)
    List<ProductoMasVendidoDTO> findProductosMasVendidos(@Param("estado") EstadoCompra estado);

    @Query("""
            SELECT new com.devsv.cinemark_api.dto.ReporteProductoDTO(
                d.producto.id,
                d.producto.nombre,
                SUM(d.cantidad),
                SUM(d.subtotal)
            )
            FROM DetalleCompra d
            WHERE d.compra.estado = :estado
            AND d.compra.fecha >= :inicio
            AND d.compra.fecha < :fin
            GROUP BY d.producto.id, d.producto.nombre
            ORDER BY SUM(d.cantidad) DESC
            """)
    List<ReporteProductoDTO> findProductosVendidosPorRango(@Param("estado") EstadoCompra estado,
            @Param("inicio") LocalDateTime inicio, @Param("fin") LocalDateTime fin);
}