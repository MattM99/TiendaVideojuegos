package com.localvideojuegos.ProyectoFinal.model.entities;

import lombok.*;

import java.time.LocalDate;
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString

public class BlacklistEntity {
    private int blacklist_id;
    private int usuario_id;
    private LocalDate fecha_inicio;
    private LocalDate fecha_fin;
    private String motivo;
}
