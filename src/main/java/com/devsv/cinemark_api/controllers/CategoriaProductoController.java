package com.devsv.cinemark_api.controllers;

import com.devsv.cinemark_api.dto.CategoriaProductoDTO;
import com.devsv.cinemark_api.interfaces.ICategoriaProductoService;
import lombok.RequiredArgsConstructor;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/categorias-productos")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class CategoriaProductoController {

    private final ICategoriaProductoService service;

    @GetMapping
    public ResponseEntity<List<CategoriaProductoDTO>> findAll() {
        return ResponseEntity.ok(service.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<CategoriaProductoDTO> findById(@PathVariable Long id) {
        return ResponseEntity.ok(service.findById(id));
    }

    @PostMapping
    public ResponseEntity<Map<String, Object>> create(@Valid @RequestBody CategoriaProductoDTO dto) {

        CategoriaProductoDTO creada = service.create(dto);

        Map<String, Object> response = new HashMap<>();
        response.put("message", "Categoría de producto registrada correctamente");
        response.put("categoria", creada);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Map<String, Object>> update(@PathVariable Long id,
            @Valid @RequestBody CategoriaProductoDTO dto) {

        CategoriaProductoDTO actualizada = service.update(id, dto);

        Map<String, Object> response = new HashMap<>();
        response.put("message", "Categoría de producto actualizada correctamente");
        response.put("categoria", actualizada);

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> delete(@PathVariable Long id) {

        service.delete(id);

        Map<String, String> response = new HashMap<>();
        response.put("message", "Categoría de producto eliminada correctamente");

        return ResponseEntity.ok(response);
    }
}