package com.devsv.cinemark_api.controllers;

import com.devsv.cinemark_api.dto.CompraDTO;
import com.devsv.cinemark_api.dto.CompraProductosRequestDTO;
import com.devsv.cinemark_api.dto.CompraRequestDTO;
import com.devsv.cinemark_api.interfaces.ICompraService;
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
@RequestMapping("/api/compras")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class CompraController {

    private final ICompraService compraService;

    // KEVIN - COMPRA: listar todas las compras (solo ADMIN)
    @GetMapping
    public ResponseEntity<List<CompraDTO>> findAll() {
        return ResponseEntity.ok(compraService.findAll());
    }

    // KEVIN - COMPRA: listar compras del cliente autenticado
    @GetMapping("/mis-compras")
    public ResponseEntity<List<CompraDTO>> misCompras(Authentication authentication) {
        return ResponseEntity.ok(compraService.findMisCompras(authentication.getName()));
    }

    // KEVIN - COMPRA: obtener detalle de una compra (valida propiedad)
    @GetMapping("/{id}")
    public ResponseEntity<CompraDTO> findById(@PathVariable Long id, Authentication authentication) {
        return ResponseEntity.ok(compraService.findById(id, authentication.getName()));
    }

    // KEVIN - COMPRA: crea nueva compra con productos, reserva stock y calcula totales con promociones
    @PostMapping
    public ResponseEntity<Map<String, Object>> create(@Valid @RequestBody CompraRequestDTO dto,
            Authentication authentication) {
        CompraDTO compra = compraService.create(dto, authentication.getName());

        Map<String, Object> response = new HashMap<>();

        response.put("message", "Compra creada correctamente");

        response.put("compra", compra);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // KEVIN - COMPRA: actualiza productos de compra pendiente, ajusta stock e inventario
    @PutMapping("/{id}/productos")
    public ResponseEntity<Map<String, Object>> actualizarProductos(@PathVariable Long id,
            @Valid @RequestBody CompraProductosRequestDTO dto, Authentication authentication) {
        CompraDTO compra = compraService.actualizarProductos(id, dto, authentication.getName());

        Map<String, Object> response = new HashMap<>();

        response.put("message", "Productos de la compra actualizados correctamente");

        response.put("compra", compra);

        return ResponseEntity.ok(response);
    }
}