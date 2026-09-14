package com.devsv.cinemark_api.controllers;

import com.devsv.cinemark_api.dto.RolDTO;
import com.devsv.cinemark_api.interfaces.IRolService;
import lombok.RequiredArgsConstructor;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/roles")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class RolController {

    private final IRolService rolService;

    @GetMapping
    public ResponseEntity<List<RolDTO>> findAll() {
        return ResponseEntity.ok(rolService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<RolDTO> findById(@PathVariable Long id) {
        return ResponseEntity.ok(rolService.findById(id));
    }

    @PostMapping
    public ResponseEntity<Map<String, Object>> create(@Valid @RequestBody RolDTO dto) {

        RolDTO creado = rolService.create(dto);

        Map<String, Object> response = new HashMap<>();
        response.put("message", "Rol registrado correctamente");
        response.put("rol", creado);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Map<String, Object>> update(@PathVariable Long id, @Valid @RequestBody RolDTO dto) {

        RolDTO actualizado = rolService.update(id, dto);

        Map<String, Object> response = new HashMap<>();
        response.put("message", "Rol actualizado correctamente");
        response.put("rol", actualizado);

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> delete(@PathVariable Long id) {

        rolService.delete(id);

        Map<String, String> response = new HashMap<>();
        response.put("message", "Rol eliminado correctamente");

        return ResponseEntity.ok(response);
    }
}