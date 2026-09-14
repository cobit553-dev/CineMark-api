package com.devsv.cinemark_api.entities;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "asientos", uniqueConstraints = {@UniqueConstraint(name = "uk_asiento_sala_fila_numero", columnNames = {"sala_id", "fila", "numero"})})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Asiento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "sala_id", nullable = false)
    private Sala sala;

    @Column(nullable = false, length = 10)
    private String fila;

    @Column(nullable = false)
    private Integer numero;

    @Column(nullable = false, length = 50)
    private String tipo;

    @Column(nullable = false)
    private Boolean estado = true;
}