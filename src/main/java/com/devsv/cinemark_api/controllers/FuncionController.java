package com.devsv.cinemark_api.controllers;

import com.devsv.cinemark_api.dto.FuncionDTO;
import com.devsv.cinemark_api.interfaces.IFuncionService;
import lombok.RequiredArgsConstructor;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/funciones")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class FuncionController {

    private final IFuncionService funcionService;

    // OSWALDO - ARQUITECTURA: controlador expone endpoints REST y delega al servicio (patrón Controller -> Service)
    @GetMapping
    public ResponseEntity<List<FuncionDTO>> findAll() {
        return ResponseEntity.ok(funcionService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<FuncionDTO> findById(@PathVariable Long id) {
        return ResponseEntity.ok(funcionService.findById(id));
    }

    @GetMapping("/pelicula/{peliculaId}")
    public ResponseEntity<List<FuncionDTO>> findByPelicula(@PathVariable Long peliculaId) {
        return ResponseEntity.ok(funcionService.findByPelicula(peliculaId));
    }

    @GetMapping("/sala/{salaId}")
    public ResponseEntity<List<FuncionDTO>> findBySala(@PathVariable Long salaId) {
        return ResponseEntity.ok(funcionService.findBySala(salaId));
    }

    @GetMapping("/fecha/{fecha}")
    public ResponseEntity<List<FuncionDTO>> findByFecha(@PathVariable LocalDate fecha) {
        return ResponseEntity.ok(funcionService.findByFecha(fecha));
    }

    @PostMapping
    public ResponseEntity<Map<String, Object>> create(@Valid @RequestBody FuncionDTO dto) {

        FuncionDTO creada = funcionService.create(dto);

        Map<String, Object> response = new HashMap<>();
        response.put("message", "Función registrada correctamente");
        response.put("funcion", creada);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Map<String, Object>> update(@PathVariable Long id, @Valid @RequestBody FuncionDTO dto) {

        FuncionDTO actualizada = funcionService.update(id, dto);

        Map<String, Object> response = new HashMap<>();
        response.put("message", "Función actualizada correctamente");
        response.put("funcion", actualizada);

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> delete(@PathVariable Long id) {

        funcionService.delete(id);

        Map<String, String> response = new HashMap<>();
        response.put("message", "Función eliminada correctamente");

        return ResponseEntity.ok(response);
    }
}