package com.devsv.cinemark_api.controllers;

import com.devsv.cinemark_api.dto.BoletoDTO;
import com.devsv.cinemark_api.dto.BoletoRequestDTO;
import com.devsv.cinemark_api.interfaces.IBoletoService;
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
@RequestMapping("/api/boletos")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class BoletoController {

    private final IBoletoService boletoService;

    @PostMapping("/reservar")
    public ResponseEntity<Map<String, Object>> reservar(@Valid @RequestBody BoletoRequestDTO dto,
            Authentication authentication) {

        BoletoDTO boleto = boletoService.reservar(dto, authentication.getName());

        Map<String, Object> response = new HashMap<>();

        response.put("message", "Asiento reservado correctamente");

        response.put("boleto", boleto);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<BoletoDTO> findById(@PathVariable Long id, Authentication authentication) {

        return ResponseEntity.ok(boletoService.findById(id, authentication.getName()));
    }

    @GetMapping("/compra/{compraId}")
    public ResponseEntity<List<BoletoDTO>> findByCompra(@PathVariable Long compraId, Authentication authentication) {

        return ResponseEntity.ok(boletoService.findByCompra(compraId, authentication.getName()));
    }

    @GetMapping("/funcion/{funcionId}")
    public ResponseEntity<List<BoletoDTO>> findByFuncion(@PathVariable Long funcionId) {

        return ResponseEntity.ok(boletoService.findByFuncion(funcionId));
    }
}