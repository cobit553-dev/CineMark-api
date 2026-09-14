package com.devsv.cinemark_api.controllers;

import com.devsv.cinemark_api.dto.*;
import com.devsv.cinemark_api.interfaces.IDashboardAdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/dashboard/admin")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class DashboardAdminController {

    private final IDashboardAdminService dashboardAdminService;

    @GetMapping
    public ResponseEntity<DashboardAdminDTO> obtenerDashboard() {

        return ResponseEntity.ok(dashboardAdminService.obtenerDashboard());
    }

    @GetMapping("/ventas-por-fecha")
    public ResponseEntity<List<VentaPorFechaDTO>> obtenerVentasPorFecha() {

        return ResponseEntity.ok(dashboardAdminService.obtenerVentasPorFecha());
    }

    @GetMapping("/productos-mas-vendidos")
    public ResponseEntity<List<ProductoMasVendidoDTO>> obtenerProductosMasVendidos() {

        return ResponseEntity.ok(dashboardAdminService.obtenerProductosMasVendidos());
    }

    @GetMapping("/peliculas-mas-vendidas")
    public ResponseEntity<List<PeliculaMasVendidaDTO>> obtenerPeliculasMasVendidas() {

        return ResponseEntity.ok(dashboardAdminService.obtenerPeliculasMasVendidas());
    }

    @GetMapping("/ocupacion-funciones")
    public ResponseEntity<List<FuncionOcupacionDTO>> obtenerOcupacionFunciones() {

        return ResponseEntity.ok(dashboardAdminService.obtenerOcupacionFunciones());
    }
}