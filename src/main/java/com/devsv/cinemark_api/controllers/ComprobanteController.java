package com.devsv.cinemark_api.controllers;

import com.devsv.cinemark_api.dto.ComprobanteDTO;
import com.devsv.cinemark_api.interfaces.IComprobanteService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/comprobantes")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class ComprobanteController {

    private final IComprobanteService comprobanteService;

    @GetMapping("/compra/{compraId}")
    public ResponseEntity<ComprobanteDTO> obtenerComprobante(@PathVariable Long compraId,
            Authentication authentication) {

        return ResponseEntity.ok(comprobanteService.obtenerComprobante(compraId, authentication.getName()));
    }
}