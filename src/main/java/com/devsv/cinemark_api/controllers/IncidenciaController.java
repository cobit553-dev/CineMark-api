package com.devsv.cinemark_api.controllers;

import com.devsv.cinemark_api.dto.IncidenciaDTO;
import com.devsv.cinemark_api.enums.EstadoIncidencia;
import com.devsv.cinemark_api.interfaces.IIncidenciaService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/incidencias")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class IncidenciaController {

    private final IIncidenciaService incidenciaService;

    @GetMapping
    public ResponseEntity<List<IncidenciaDTO>> findAll() {

        return ResponseEntity.ok(incidenciaService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<IncidenciaDTO> findById(@PathVariable Long id) {

        return ResponseEntity.ok(incidenciaService.findById(id));
    }

    @PostMapping
    public ResponseEntity<IncidenciaDTO> create(@RequestBody IncidenciaDTO incidenciaDTO) {

        return ResponseEntity.status(HttpStatus.CREATED).body(incidenciaService.create(incidenciaDTO));
    }

    @PutMapping("/{id}")
    public ResponseEntity<IncidenciaDTO> update(@PathVariable Long id, @RequestBody IncidenciaDTO incidenciaDTO) {

        return ResponseEntity.ok(incidenciaService.update(id, incidenciaDTO));
    }

    @GetMapping("/sala/{salaId}")
    public ResponseEntity<List<IncidenciaDTO>> findBySala(@PathVariable Long salaId) {

        return ResponseEntity.ok(incidenciaService.findBySala(salaId));
    }

    @GetMapping("/estado/{estado}")
    public ResponseEntity<List<IncidenciaDTO>> findByEstado(@PathVariable EstadoIncidencia estado) {

        return ResponseEntity.ok(incidenciaService.findByEstado(estado));
    }
}