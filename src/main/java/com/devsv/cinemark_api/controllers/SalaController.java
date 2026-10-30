package com.devsv.cinemark_api.controllers;

import com.devsv.cinemark_api.dto.SalaDTO;
import com.devsv.cinemark_api.interfaces.ISalaService;
import lombok.RequiredArgsConstructor;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/salas")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class SalaController {

    private final ISalaService salaService;

    @GetMapping
    public ResponseEntity<List<SalaDTO>> findAll() {
        return ResponseEntity.ok(salaService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<SalaDTO> findById(@PathVariable Long id) {
        return ResponseEntity.ok(salaService.findById(id));
    }

    @PostMapping
    public ResponseEntity<Map<String, Object>> create(@Valid @RequestBody SalaDTO dto) {

        SalaDTO creada = salaService.create(dto);

        Map<String, Object> response = new HashMap<>();
        response.put("message", "Sala registrada correctamente");
        response.put("sala", creada);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Map<String, Object>> update(@PathVariable Long id, @Valid @RequestBody SalaDTO dto) {

        SalaDTO actualizada = salaService.update(id, dto);

        Map<String, Object> response = new HashMap<>();
        response.put("message", "Sala actualizada correctamente");
        response.put("sala", actualizada);

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> delete(@PathVariable Long id) {

        salaService.delete(id);

        Map<String, String> response = new HashMap<>();
        response.put("message", "Sala eliminada correctamente");

        return ResponseEntity.ok(response);
    }
}