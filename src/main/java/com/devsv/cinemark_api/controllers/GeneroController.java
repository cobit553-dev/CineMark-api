package com.devsv.cinemark_api.controllers;

import com.devsv.cinemark_api.dto.GeneroDTO;
import com.devsv.cinemark_api.interfaces.IGeneroService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/generos")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class GeneroController {

    private final IGeneroService generoService;

    @GetMapping
    public ResponseEntity<List<GeneroDTO>> findAll() {
        return ResponseEntity.ok(generoService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<GeneroDTO> findById(@PathVariable Long id) {
        return ResponseEntity.ok(generoService.findById(id));
    }

    @PostMapping
    public ResponseEntity<Map<String, Object>> create(@RequestBody GeneroDTO generoDTO) {
        GeneroDTO creado = generoService.create(generoDTO);

        Map<String, Object> response = new HashMap<>();
        response.put("message", "Género registrado correctamente");
        response.put("genero", creado);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Map<String, Object>> update(@PathVariable Long id, @RequestBody GeneroDTO generoDTO) {
        GeneroDTO actualizado = generoService.update(id, generoDTO);

        Map<String, Object> response = new HashMap<>();
        response.put("message", "Género actualizado correctamente");
        response.put("genero", actualizado);

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> delete(@PathVariable Long id) {
        generoService.delete(id);

        Map<String, String> response = new HashMap<>();
        response.put("message", "Género eliminado correctamente");

        return ResponseEntity.ok(response);
    }
}