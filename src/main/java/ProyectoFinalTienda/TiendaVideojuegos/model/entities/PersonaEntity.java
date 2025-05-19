package com.localvideojuegos.ProyectoFinal.model.entities;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString

public class PersonaEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int persona_id;

    @Column(
            nullable = false
    )
    private String nombre;

    @Column(
            nullable = false
    )
    private String apellido;

    @Column(
            nullable = false,
            unique = true
    )
    private String dni;

    @Column(
            nullable = false,
            unique = true
    )
    private String email;

    @Column(
            nullable = false
    )
    private String telefono;
}
