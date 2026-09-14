package com.devsv.cinemark_api.controllers;

import com.devsv.cinemark_api.dto.auth.LoginRequestDTO;
import com.devsv.cinemark_api.dto.auth.LoginResponseDTO;
import com.devsv.cinemark_api.dto.auth.RegistroClienteDTO;
import com.devsv.cinemark_api.interfaces.IAuthService;
import lombok.RequiredArgsConstructor;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class AuthController {

    private final IAuthService authService;

    // DANIEL - SEGURIDAD: endpoint público para registro de nuevos clientes (rol CLIENTE por defecto)
    @PostMapping("/register")
    public ResponseEntity<Map<String, Object>> register(@Valid @RequestBody RegistroClienteDTO dto) {

        LoginResponseDTO auth = authService.registrarCliente(dto);

        Map<String, Object> response = new HashMap<>();
        response.put("message", "Registro realizado correctamente");
        response.put("auth", auth);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // DANIEL - SEGURIDAD: endpoint público para login, valida credenciales y retorna JWT
    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(@Valid @RequestBody LoginRequestDTO dto) {

        LoginResponseDTO auth = authService.login(dto);

        Map<String, Object> response = new HashMap<>();
        response.put("message", "Inicio de sesión exitoso");
        response.put("auth", auth);

        return ResponseEntity.ok(response);
    }
}