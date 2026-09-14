package com.devsv.cinemark_api.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CompraProductosRequestDTO {

    @NotNull(message = "La lista de productos es obligatoria")
    private List<@Valid DetalleCompraDTO> detalles;
}