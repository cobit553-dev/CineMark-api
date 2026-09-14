package com.devsv.cinemark_api.controllers;

import com.devsv.cinemark_api.dto.MovimientoInventarioDTO;
import com.devsv.cinemark_api.interfaces.IMovimientoInventarioService;
import lombok.RequiredArgsConstructor;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/movimientos-inventario")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class MovimientoInventarioController {

    private final IMovimientoInventarioService movimientoService;

    @GetMapping
    public ResponseEntity<List<MovimientoInventarioDTO>> findAll() {

        return ResponseEntity.ok(movimientoService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<MovimientoInventarioDTO> findById(@PathVariable Long id) {

        return ResponseEntity.ok(movimientoService.findById(id));
    }

    @GetMapping("/inventario/{inventarioId}")
    public ResponseEntity<List<MovimientoInventarioDTO>> findByInventario(@PathVariable Long inventarioId) {

        return ResponseEntity.ok(movimientoService.findByInventario(inventarioId));
    }

    @PostMapping
    public ResponseEntity<Map<String, Object>> create(@Valid @RequestBody MovimientoInventarioDTO dto,
            Authentication authentication) {

        MovimientoInventarioDTO creado = movimientoService.create(dto, authentication.getName());

        Map<String, Object> response = new HashMap<>();

        response.put("message", "Movimiento de inventario registrado correctamente");

        response.put("movimiento", creado);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}