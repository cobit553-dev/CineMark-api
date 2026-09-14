package com.devsv.cinemark_api.controllers;

import com.devsv.cinemark_api.dto.HistorialClienteDTO;
import com.devsv.cinemark_api.interfaces.IHistorialClienteService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/historial")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class HistorialClienteController {

    private final IHistorialClienteService historialClienteService;

    @GetMapping
    public ResponseEntity<HistorialClienteDTO> obtenerHistorial(Authentication authentication) {

        return ResponseEntity.ok(historialClienteService.obtenerHistorial(authentication.getName()));
    }
}