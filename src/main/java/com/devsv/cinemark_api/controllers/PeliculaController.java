package com.devsv.cinemark_api.controllers;

import com.devsv.cinemark_api.dto.PeliculaDTO;
import com.devsv.cinemark_api.dto.PeliculaRequestDTO;
import com.devsv.cinemark_api.interfaces.IPeliculaService;
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
@RequestMapping("/api/peliculas")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class PeliculaController {

    private final IPeliculaService peliculaService;

    @GetMapping
    public ResponseEntity<List<PeliculaDTO>> findAll() {

        return ResponseEntity.ok(peliculaService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<PeliculaDTO> findById(@PathVariable Long id) {

        return ResponseEntity.ok(peliculaService.findById(id));
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Map<String, Object>> create(@Valid @ModelAttribute PeliculaRequestDTO dto) {

        PeliculaDTO creada = peliculaService.create(dto);

        Map<String, Object> response = new HashMap<>();

        response.put("message", "Película registrada correctamente");

        response.put("pelicula", creada);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Map<String, Object>> update(@PathVariable Long id,
            @Valid @ModelAttribute PeliculaRequestDTO dto) {

        PeliculaDTO actualizada = peliculaService.update(id, dto);

        Map<String, Object> response = new HashMap<>();

        response.put("message", "Película actualizada correctamente");

        response.put("pelicula", actualizada);

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> delete(@PathVariable Long id) {

        peliculaService.delete(id);

        Map<String, String> response = new HashMap<>();

        response.put("message", "Película eliminada correctamente");

        return ResponseEntity.ok(response);
    }
}