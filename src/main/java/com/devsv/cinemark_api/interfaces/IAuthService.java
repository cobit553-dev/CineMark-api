package com.devsv.cinemark_api.interfaces;

import com.devsv.cinemark_api.dto.auth.LoginRequestDTO;
import com.devsv.cinemark_api.dto.auth.LoginResponseDTO;
import com.devsv.cinemark_api.dto.auth.RegistroClienteDTO;

public interface IAuthService {

    LoginResponseDTO registrarCliente(RegistroClienteDTO dto);

    LoginResponseDTO login(LoginRequestDTO dto);
}