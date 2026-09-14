package com.devsv.cinemark_api.interfaces;

import com.devsv.cinemark_api.dto.*;

import java.util.List;

public interface IDashboardAdminService {

    DashboardAdminDTO obtenerDashboard();

    List<VentaPorFechaDTO> obtenerVentasPorFecha();

    List<ProductoMasVendidoDTO> obtenerProductosMasVendidos();

    List<PeliculaMasVendidaDTO> obtenerPeliculasMasVendidas();

    List<FuncionOcupacionDTO> obtenerOcupacionFunciones();
}