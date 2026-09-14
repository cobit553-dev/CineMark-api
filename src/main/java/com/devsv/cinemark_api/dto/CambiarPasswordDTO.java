package com.devsv.cinemark_api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CambiarPasswordDTO {

    private String passwordActual;

    private String passwordNueva;
}