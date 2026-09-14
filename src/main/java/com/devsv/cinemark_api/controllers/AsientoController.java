package com.devsv.cinemark_api.controllers;

import com.devsv.cinemark_api.dto.AsientoDTO;
import com.devsv.cinemark_api.dto.AsientoDisponibilidadDTO;
import com.devsv.cinemark_api.dto.GenerarAsientosRequestDTO;
import com.devsv.cinemark_api.interfaces.IAsientoService;
import lombok.RequiredArgsConstructor;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/asientos")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class AsientoController {

    private final IAsientoService asientoService;

    @GetMapping
    public ResponseEntity<List<AsientoDTO>> findAll() {
        return ResponseEntity.ok(asientoService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<AsientoDTO> findById(@PathVariable Long id) {
        return ResponseEntity.ok(asientoService.findById(id));
    }

    @GetMapping("/sala/{salaId}")
    public ResponseEntity<List<AsientoDTO>> findBySala(@PathVariable Long salaId) {
        return ResponseEntity.ok(asientoService.findBySala(salaId));
    }

    @GetMapping("/funcion/{funcionId}/disponibilidad")
    public ResponseEntity<List<AsientoDisponibilidadDTO>> findDisponibilidadByFuncion(@PathVariable Long funcionId) {
        return ResponseEntity.ok(asientoService.findDisponibilidadByFuncion(funcionId));
    }

    @PostMapping
    public ResponseEntity<Map<String, Object>> create(@Valid @RequestBody AsientoDTO dto) {
        AsientoDTO creado = asientoService.create(dto);

        Map<String, Object> response = new HashMap<>();
        response.put("message", "Asiento registrado correctamente");
        response.put("asiento", creado);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/generar")
    public ResponseEntity<Map<String, Object>> generar(@Valid @RequestBody GenerarAsientosRequestDTO dto) {
        List<AsientoDTO> creados = asientoService.generar(dto);

        Map<String, Object> response = new HashMap<>();

        if (creados.isEmpty()) {
            response.put("message", "No se crearon asientos nuevos porque todos ya estaban registrados");
        } else {
            response.put("message", creados.size() + " asiento(s) generado(s) correctamente");
        }

        response.put("cantidad", creados.size());
        response.put("asientos", creados);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Map<String, Object>> update(@PathVariable Long id, @Valid @RequestBody AsientoDTO dto) {
        AsientoDTO actualizado = asientoService.update(id, dto);

        Map<String, Object> response = new HashMap<>();
        response.put("message", "Asiento actualizado correctamente");
        response.put("asiento", actualizado);

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> delete(@PathVariable Long id) {
        asientoService.delete(id);

        Map<String, String> response = new HashMap<>();
        response.put("message", "Asiento eliminado correctamente");

        return ResponseEntity.ok(response);
    }
}