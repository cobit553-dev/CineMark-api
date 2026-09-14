package com.devsv.cinemark_api.controllers;

import com.devsv.cinemark_api.dto.PerfilClienteUpdateDTO;
import com.devsv.cinemark_api.dto.UsuarioDTO;
import com.devsv.cinemark_api.interfaces.IUsuarioService;
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
@RequestMapping("/api/usuarios")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class UsuarioController {

    private final IUsuarioService usuarioService;

    @GetMapping
    public ResponseEntity<List<UsuarioDTO>> findAll() {
        return ResponseEntity.ok(usuarioService.findAll());
    }

    @GetMapping("/perfil")
    public ResponseEntity<UsuarioDTO> findPerfil(Authentication authentication) {

        return ResponseEntity.ok(usuarioService.findPerfil(authentication.getName()));
    }

    @PutMapping("/perfil")
    public ResponseEntity<Map<String, Object>> updatePerfil(@Valid @RequestBody PerfilClienteUpdateDTO dto,
            Authentication authentication) {

        UsuarioDTO actualizado = usuarioService.updatePerfil(authentication.getName(), dto);

        Map<String, Object> response = new HashMap<>();

        response.put("message", "Perfil actualizado correctamente");

        response.put("usuario", actualizado);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<UsuarioDTO> findById(@PathVariable Long id) {

        return ResponseEntity.ok(usuarioService.findById(id));
    }

    @PostMapping
    public ResponseEntity<Map<String, Object>> create(@Valid @RequestBody UsuarioDTO dto) {

        UsuarioDTO creado = usuarioService.create(dto);

        Map<String, Object> response = new HashMap<>();

        response.put("message", "Usuario registrado correctamente");

        response.put("usuario", creado);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Map<String, Object>> update(@PathVariable Long id, @Valid @RequestBody UsuarioDTO dto) {

        UsuarioDTO actualizado = usuarioService.update(id, dto);

        Map<String, Object> response = new HashMap<>();

        response.put("message", "Usuario actualizado correctamente");

        response.put("usuario", actualizado);

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> delete(@PathVariable Long id) {

        usuarioService.delete(id);

        Map<String, String> response = new HashMap<>();

        response.put("message", "Usuario eliminado correctamente");

        return ResponseEntity.ok(response);
    }
}