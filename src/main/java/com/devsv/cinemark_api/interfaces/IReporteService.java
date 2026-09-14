package com.devsv.cinemark_api.interfaces;

import com.devsv.cinemark_api.dto.ReporteInventarioDTO;
import com.devsv.cinemark_api.dto.ReportePeliculaDTO;
import com.devsv.cinemark_api.dto.ReporteProductoDTO;
import com.devsv.cinemark_api.dto.ReporteVentasDTO;

import java.time.LocalDate;
import java.util.List;

public interface IReporteService {

    ReporteVentasDTO obtenerVentasPorRango(LocalDate fechaInicio, LocalDate fechaFin);

    List<ReporteProductoDTO> obtenerProductosPorRango(LocalDate fechaInicio, LocalDate fechaFin);

    List<ReportePeliculaDTO> obtenerPeliculasPorRango(LocalDate fechaInicio, LocalDate fechaFin);

    List<ReporteInventarioDTO> obtenerInventarioBajo();
}