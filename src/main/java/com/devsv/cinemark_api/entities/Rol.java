package com.devsv.cinemark_api.entities;

import com.devsv.cinemark_api.enums.NombreRol;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "roles")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Rol {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, unique = true, length = 20)
    private NombreRol nombre;

    @Column(length = 255)
    private String descripcion;

    @Column(nullable = false)
    private Boolean estado = true;
}