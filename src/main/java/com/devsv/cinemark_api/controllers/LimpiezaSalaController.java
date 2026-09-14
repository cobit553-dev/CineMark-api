package com.devsv.cinemark_api.controllers;

import com.devsv.cinemark_api.dto.LimpiezaSalaDTO;
import com.devsv.cinemark_api.enums.EstadoLimpieza;
import com.devsv.cinemark_api.interfaces.ILimpiezaSalaService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/limpieza-salas")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class LimpiezaSalaController {

    private final ILimpiezaSalaService limpiezaSalaService;

    @GetMapping
    public ResponseEntity<List<LimpiezaSalaDTO>> findAll() {

        return ResponseEntity.ok(limpiezaSalaService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<LimpiezaSalaDTO> findById(@PathVariable Long id) {

        return ResponseEntity.ok(limpiezaSalaService.findById(id));
    }

    @PostMapping
    public ResponseEntity<LimpiezaSalaDTO> create(@RequestBody LimpiezaSalaDTO limpiezaSalaDTO) {

        return ResponseEntity.status(HttpStatus.CREATED).body(limpiezaSalaService.create(limpiezaSalaDTO));
    }

    @PutMapping("/{id}")
    public ResponseEntity<LimpiezaSalaDTO> update(@PathVariable Long id, @RequestBody LimpiezaSalaDTO limpiezaSalaDTO) {

        return ResponseEntity.ok(limpiezaSalaService.update(id, limpiezaSalaDTO));
    }

    @GetMapping("/sala/{salaId}")
    public ResponseEntity<List<LimpiezaSalaDTO>> findBySala(@PathVariable Long salaId) {

        return ResponseEntity.ok(limpiezaSalaService.findBySala(salaId));
    }

    @GetMapping("/estado/{estado}")
    public ResponseEntity<List<LimpiezaSalaDTO>> findByEstado(@PathVariable EstadoLimpieza estado) {

        return ResponseEntity.ok(limpiezaSalaService.findByEstado(estado));
    }
}