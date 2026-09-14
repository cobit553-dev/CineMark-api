package com.devsv.cinemark_api.controllers;

import com.devsv.cinemark_api.dto.PagoDTO;
import com.devsv.cinemark_api.dto.PagoRequestDTO;
import com.devsv.cinemark_api.interfaces.IPagoService;
import lombok.RequiredArgsConstructor;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/pagos")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class PagoController {

    private final IPagoService pagoService;

    @GetMapping
    public ResponseEntity<List<PagoDTO>> findAll() {

        return ResponseEntity.ok(pagoService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<PagoDTO> findById(@PathVariable Long id, Authentication authentication) {

        return ResponseEntity.ok(pagoService.findById(id, authentication.getName()));
    }

    @GetMapping("/compra/{compraId}")
    public ResponseEntity<PagoDTO> findByCompra(@PathVariable Long compraId, Authentication authentication) {

        return ResponseEntity.ok(pagoService.findByCompra(compraId, authentication.getName()));
    }

    @PostMapping
    public ResponseEntity<Map<String, Object>> realizarPago(@Valid @RequestBody PagoRequestDTO dto,
            Authentication authentication) {

        PagoDTO pago = pagoService.realizarPago(dto, authentication.getName());

        Map<String, Object> response = new HashMap<>();

        response.put("message", "Pago realizado correctamente");

        response.put("pago", pago);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}