package com.devsv.cinemark_api.dto;

import jakarta.validation.Valid;
import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CompraRequestDTO {

    private Long promocionId;

    private List<@Valid DetalleCompraDTO> detalles;
}