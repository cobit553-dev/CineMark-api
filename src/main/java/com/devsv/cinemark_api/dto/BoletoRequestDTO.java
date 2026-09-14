package com.devsv.cinemark_api.dto;

import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BoletoRequestDTO {

    @NotNull(message = "La compra es obligatoria")
    private Long compraId;

    @NotNull(message = "La función es obligatoria")
    private Long funcionId;

    @NotNull(message = "El asiento es obligatorio")
    private Long asientoId;
}