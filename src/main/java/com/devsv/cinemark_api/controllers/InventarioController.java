package com.devsv.cinemark_api.controllers;

import com.devsv.cinemark_api.dto.InventarioDTO;
import com.devsv.cinemark_api.interfaces.IInventarioService;
import lombok.RequiredArgsConstructor;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/inventarios")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class InventarioController {

    private final IInventarioService inventarioService;

    @GetMapping
    public ResponseEntity<List<InventarioDTO>> findAll() {
        return ResponseEntity.ok(inventarioService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<InventarioDTO> findById(@PathVariable Long id) {
        return ResponseEntity.ok(inventarioService.findById(id));
    }

    @GetMapping("/producto/{productoId}")
    public ResponseEntity<InventarioDTO> findByProducto(@PathVariable Long productoId) {
        return ResponseEntity.ok(inventarioService.findByProducto(productoId));
    }

    @PostMapping
    public ResponseEntity<Map<String, Object>> create(@Valid @RequestBody InventarioDTO dto) {

        InventarioDTO creado = inventarioService.create(dto);

        Map<String, Object> response = new HashMap<>();
        response.put("message", "Inventario registrado correctamente");
        response.put("inventario", creado);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Map<String, Object>> update(@PathVariable Long id, @Valid @RequestBody InventarioDTO dto) {

        InventarioDTO actualizado = inventarioService.update(id, dto);

        Map<String, Object> response = new HashMap<>();
        response.put("message", "Inventario actualizado correctamente");
        response.put("inventario", actualizado);

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> delete(@PathVariable Long id) {

        inventarioService.delete(id);

        Map<String, String> response = new HashMap<>();
        response.put("message", "Inventario eliminado correctamente");

        return ResponseEntity.ok(response);
    }
}