package com.devsv.cinemark_api.dto.auth;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class LoginResponseDTO {

    private String token;
    private Long usuarioId;
    private String email;
    private String nombre;
    private String apellido;
    private String rol;
}