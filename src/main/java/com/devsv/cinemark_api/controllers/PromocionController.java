package com.devsv.cinemark_api.controllers;

import com.devsv.cinemark_api.dto.PromocionDTO;
import com.devsv.cinemark_api.interfaces.IPromocionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/promociones")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class PromocionController {

    private final IPromocionService promocionService;

    @GetMapping
    public ResponseEntity<List<PromocionDTO>> findAll() {

        return ResponseEntity.ok(promocionService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<PromocionDTO> findById(@PathVariable Long id) {

        return ResponseEntity.ok(promocionService.findById(id));
    }

    @GetMapping("/vigentes")
    public ResponseEntity<List<PromocionDTO>> findVigentes() {

        return ResponseEntity.ok(promocionService.findVigentes());
    }

    @PostMapping
    public ResponseEntity<PromocionDTO> create(@RequestBody PromocionDTO promocionDTO) {

        return ResponseEntity.status(HttpStatus.CREATED).body(promocionService.create(promocionDTO));
    }

    @PutMapping("/{id}")
    public ResponseEntity<PromocionDTO> update(@PathVariable Long id, @RequestBody PromocionDTO promocionDTO) {

        return ResponseEntity.ok(promocionService.update(id, promocionDTO));
    }
}