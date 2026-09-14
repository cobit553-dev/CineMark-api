package com.devsv.cinemark_api.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Service
public class JwtService {

    @Value("${app.jwt.secret}")
    private String secret;

    @Value("${app.jwt.expiration-ms}")
    private long expirationMs;

    private SecretKey obtenerLlave() {
        return Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    // DANIEL - JWT: genera token firmado con claims de usuario (id, email, nombre, apellido, rol)
    public String generarToken(UsuarioPrincipal principal) {

        Date ahora = new Date();
        Date expiracion = new Date(ahora.getTime() + expirationMs);

        return Jwts.builder().subject(principal.getUsername()).claim("usuarioId", principal.getId())
                .claim("nombre", principal.getNombre()).claim("apellido", principal.getApellido())
                .claim("rol", principal.getRol()).issuedAt(ahora).expiration(expiracion).signWith(obtenerLlave())
                .compact();
    }

    // DANIEL - JWT: valida firma y expiración, retorna claims para extraer datos del usuario
    public Claims validarYExtraerClaims(String token) {

        return Jwts.parser().verifyWith(obtenerLlave()).build().parseSignedClaims(token).getPayload();
    }
}