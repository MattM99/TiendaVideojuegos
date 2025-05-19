package com.localvideojuegos.ProyectoFinal.model.entities;

import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString

public class AlquilerEntity {
    private int alquiler_id;
    private int persona_id;
    private LocalDate fecha_retiro;
    private LocalDate fecha_devolucion;

}
