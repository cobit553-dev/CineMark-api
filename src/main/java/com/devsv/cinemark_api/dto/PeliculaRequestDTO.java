package com.devsv.cinemark_api.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class PeliculaRequestDTO {

    @NotBlank(message = "El título es obligatorio")
    private String titulo;

    @NotBlank(message = "La clasificación es obligatoria")
    private String clasificacion;

    @NotNull(message = "La duración es obligatoria")
    @Min(value = 1, message = "La duración debe ser mayor que cero")
    private Integer duracion;

    private String sinopsis;

    private MultipartFile imagen;

    private String trailerUrl;

    @NotNull(message = "La fecha de estreno es obligatoria")
    private LocalDate fechaEstreno;

    @NotNull(message = "La fecha final de exhibición es obligatoria")
    private LocalDate fechaFinExhibicion;

    private Boolean estado;

    private List<Long> generosIds;
}