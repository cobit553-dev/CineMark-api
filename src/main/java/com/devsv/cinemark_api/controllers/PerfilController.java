package com.devsv.cinemark_api.controllers;

import com.devsv.cinemark_api.dto.CambiarPasswordDTO;
import com.devsv.cinemark_api.dto.PerfilDTO;
import com.devsv.cinemark_api.dto.PerfilUpdateDTO;
import com.devsv.cinemark_api.interfaces.IPerfilService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/perfil")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class PerfilController {

    private final IPerfilService perfilService;

    @GetMapping
    public ResponseEntity<PerfilDTO> obtenerPerfil(Authentication authentication) {

        return ResponseEntity.ok(perfilService.obtenerPerfil(authentication.getName()));
    }

    @PutMapping
    public ResponseEntity<PerfilDTO> actualizarPerfil(@RequestBody PerfilUpdateDTO dto, Authentication authentication) {

        return ResponseEntity.ok(perfilService.actualizarPerfil(dto, authentication.getName()));
    }

    @PutMapping("/password")
    public ResponseEntity<Map<String, String>> cambiarPassword(@RequestBody CambiarPasswordDTO dto,
            Authentication authentication) {

        perfilService.cambiarPassword(dto, authentication.getName());

        Map<String, String> response = new HashMap<>();

        response.put("message", "Contraseña actualizada correctamente");

        return ResponseEntity.ok(response);
    }
}