package com.devsv.cinemark_api.controllers;

import com.devsv.cinemark_api.dto.ProductoDTO;
import com.devsv.cinemark_api.dto.ProductoRequestDTO;
import com.devsv.cinemark_api.interfaces.IProductoService;
import lombok.RequiredArgsConstructor;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/productos")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class ProductoController {

    private final IProductoService productoService;

    @GetMapping
    public ResponseEntity<List<ProductoDTO>> findAll() {
        return ResponseEntity.ok(productoService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductoDTO> findById(@PathVariable Long id) {
        return ResponseEntity.ok(productoService.findById(id));
    }

    @GetMapping("/categoria/{categoriaId}")
    public ResponseEntity<List<ProductoDTO>> findByCategoria(@PathVariable Long categoriaId) {
        return ResponseEntity.ok(productoService.findByCategoria(categoriaId));
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Map<String, Object>> create(@Valid @ModelAttribute ProductoRequestDTO dto) {
        ProductoDTO creado = productoService.create(dto);

        Map<String, Object> response = new HashMap<>();

        response.put("message", "Producto registrado correctamente");

        response.put("producto", creado);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Map<String, Object>> update(@PathVariable Long id,
            @Valid @ModelAttribute ProductoRequestDTO dto) {
        ProductoDTO actualizado = productoService.update(id, dto);

        Map<String, Object> response = new HashMap<>();

        response.put("message", "Producto actualizado correctamente");

        response.put("producto", actualizado);

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> delete(@PathVariable Long id) {
        productoService.delete(id);

        Map<String, String> response = new HashMap<>();

        response.put("message", "Producto eliminado correctamente");

        return ResponseEntity.ok(response);
    }
}