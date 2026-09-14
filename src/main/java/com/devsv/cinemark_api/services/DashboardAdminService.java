package com.devsv.cinemark_api.services;

import com.devsv.cinemark_api.dto.*;
import com.devsv.cinemark_api.enums.EstadoBoleto;
import com.devsv.cinemark_api.enums.EstadoCompra;
import com.devsv.cinemark_api.interfaces.IDashboardAdminService;
import com.devsv.cinemark_api.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DashboardAdminService implements IDashboardAdminService {

    private final UsuarioRepository usuarioRepository;
    private final PeliculaRepository peliculaRepository;
    private final FuncionRepository funcionRepository;
    private final ProductoRepository productoRepository;
    private final CompraRepository compraRepository;
    private final BoletoRepository boletoRepository;
    private final InventarioRepository inventarioRepository;
    private final DetalleCompraRepository detalleCompraRepository;

    @Override
    @Transactional(readOnly = true)
    public DashboardAdminDTO obtenerDashboard() {
        long totalUsuarios = usuarioRepository.count();
        long totalPeliculas = peliculaRepository.count();
        long totalFunciones = funcionRepository.count();
        long totalProductos = productoRepository.count();
        long totalCompras = compraRepository.count();

        long comprasPendientes = compraRepository.countByEstado(EstadoCompra.PENDIENTE);
        long comprasPagadas = compraRepository.countByEstado(EstadoCompra.PAGADA);
        long comprasCanceladas = compraRepository.countByEstado(EstadoCompra.CANCELADA);

        long boletosVendidos = boletoRepository.countByEstado(EstadoBoleto.VENDIDO);
        long productosStockBajo = inventarioRepository.countProductosStockBajo();

        BigDecimal ingresosTotales = compraRepository.sumTotalByEstado(EstadoCompra.PAGADA);

        if (ingresosTotales == null) {
            ingresosTotales = BigDecimal.ZERO;
        }

        return DashboardAdminDTO.builder().totalUsuarios(totalUsuarios).totalPeliculas(totalPeliculas)
                .totalFunciones(totalFunciones).totalProductos(totalProductos).totalCompras(totalCompras)
                .comprasPendientes(comprasPendientes).comprasPagadas(comprasPagadas)
                .comprasCanceladas(comprasCanceladas).boletosVendidos(boletosVendidos)
                .productosStockBajo(productosStockBajo).ingresosTotales(ingresosTotales).build();
    }

    @Override
    @Transactional(readOnly = true)
    public List<VentaPorFechaDTO> obtenerVentasPorFecha() {
        return compraRepository.findVentasPorFecha(EstadoCompra.PAGADA);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductoMasVendidoDTO> obtenerProductosMasVendidos() {
        return detalleCompraRepository.findProductosMasVendidos(EstadoCompra.PAGADA);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PeliculaMasVendidaDTO> obtenerPeliculasMasVendidas() {
        return boletoRepository.findPeliculasMasVendidas(EstadoBoleto.VENDIDO);
    }

    @Override
    @Transactional(readOnly = true)
    public List<FuncionOcupacionDTO> obtenerOcupacionFunciones() {
        return funcionRepository.findDatosOcupacionFunciones(EstadoBoleto.VENDIDO).stream()
                .map(this::convertirOcupacion).toList();
    }

    private FuncionOcupacionDTO convertirOcupacion(Object[] fila) {
        Long funcionId = (Long) fila[0];
        String peliculaTitulo = (String) fila[1];
        String salaNombre = (String) fila[2];
        LocalDate fecha = (LocalDate) fila[3];
        LocalTime horaInicio = (LocalTime) fila[4];
        Long boletosVendidos = (Long) fila[5];
        Integer capacidadSala = (Integer) fila[6];

        BigDecimal porcentajeOcupacion = calcularPorcentajeOcupacion(boletosVendidos, capacidadSala);

        return FuncionOcupacionDTO.builder().funcionId(funcionId).peliculaTitulo(peliculaTitulo).salaNombre(salaNombre)
                .fecha(fecha).horaInicio(horaInicio).boletosVendidos(boletosVendidos).capacidadSala(capacidadSala)
                .porcentajeOcupacion(porcentajeOcupacion).build();
    }

    private BigDecimal calcularPorcentajeOcupacion(Long boletosVendidos, Integer capacidadSala) {
        if (capacidadSala == null || capacidadSala <= 0) {
            return BigDecimal.ZERO;
        }

        long vendidos = boletosVendidos != null ? boletosVendidos : 0L;

        return BigDecimal.valueOf(vendidos).multiply(BigDecimal.valueOf(100))
                .divide(BigDecimal.valueOf(capacidadSala), 2, RoundingMode.HALF_UP);
    }
}