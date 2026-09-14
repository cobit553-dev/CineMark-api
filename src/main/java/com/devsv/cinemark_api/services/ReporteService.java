package com.devsv.cinemark_api.services;

import com.devsv.cinemark_api.dto.ReporteInventarioDTO;
import com.devsv.cinemark_api.dto.ReportePeliculaDTO;
import com.devsv.cinemark_api.dto.ReporteProductoDTO;
import com.devsv.cinemark_api.dto.ReporteVentasDTO;
import com.devsv.cinemark_api.enums.EstadoBoleto;
import com.devsv.cinemark_api.enums.EstadoCompra;
import com.devsv.cinemark_api.exceptions.BadRequestException;
import com.devsv.cinemark_api.interfaces.IReporteService;
import com.devsv.cinemark_api.repository.BoletoRepository;
import com.devsv.cinemark_api.repository.CompraRepository;
import com.devsv.cinemark_api.repository.DetalleCompraRepository;
import com.devsv.cinemark_api.repository.InventarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ReporteService implements IReporteService {

    private final CompraRepository compraRepository;
    private final DetalleCompraRepository detalleCompraRepository;
    private final BoletoRepository boletoRepository;
    private final InventarioRepository inventarioRepository;

    @Override
    @Transactional(readOnly = true)
    public ReporteVentasDTO obtenerVentasPorRango(LocalDate fechaInicio, LocalDate fechaFin) {
        validarFechas(fechaInicio, fechaFin);

        LocalDateTime inicio = fechaInicio.atStartOfDay();
        LocalDateTime fin = fechaFin.plusDays(1).atStartOfDay();

        long cantidadCompras = compraRepository.countByEstadoAndFechaGreaterThanEqualAndFechaLessThan(EstadoCompra.PAGADA, inicio, fin);

        BigDecimal totalVentas = compraRepository.sumTotalByEstadoAndFecha(EstadoCompra.PAGADA, inicio, fin);

        return ReporteVentasDTO.builder().fechaInicio(fechaInicio).fechaFin(fechaFin).cantidadCompras(cantidadCompras)
                .totalVentas(totalVentas != null ? totalVentas : BigDecimal.ZERO).build();
    }

    @Override
    @Transactional(readOnly = true)
    public List<ReporteProductoDTO> obtenerProductosPorRango(LocalDate fechaInicio, LocalDate fechaFin) {
        validarFechas(fechaInicio, fechaFin);

        LocalDateTime inicio = fechaInicio.atStartOfDay();
        LocalDateTime fin = fechaFin.plusDays(1).atStartOfDay();

        return detalleCompraRepository.findProductosVendidosPorRango(EstadoCompra.PAGADA, inicio, fin);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ReportePeliculaDTO> obtenerPeliculasPorRango(LocalDate fechaInicio, LocalDate fechaFin) {
        validarFechas(fechaInicio, fechaFin);

        LocalDateTime inicio = fechaInicio.atStartOfDay();
        LocalDateTime fin = fechaFin.plusDays(1).atStartOfDay();

        return boletoRepository.findPeliculasVendidasPorRango(EstadoBoleto.VENDIDO, inicio, fin);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ReporteInventarioDTO> obtenerInventarioBajo() {
        return inventarioRepository.findProductosConStockBajo();
    }

    private void validarFechas(LocalDate fechaInicio, LocalDate fechaFin) {
        if (fechaInicio == null || fechaFin == null) {
            throw new BadRequestException("La fecha de inicio y la fecha de fin son obligatorias");
        }

        if (fechaFin.isBefore(fechaInicio)) {
            throw new BadRequestException("La fecha de fin no puede ser anterior a la fecha de inicio");
        }
    }
}