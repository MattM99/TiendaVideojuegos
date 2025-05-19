package com.localvideojuegos.ProyectoFinal.model.entities;
import com.localvideojuegos.ProyectoFinal.model.enums.Generos;
import com.localvideojuegos.ProyectoFinal.model.enums.Plataformas;
import lombok.*;

import java.time.Year;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString

public class VideojuegoEntity {
    private int videojuegoID;
    private String titulo;
    private String desarrollador;
    private Generos genero;
    private Plataformas plataforma;
    private Year lanzamiento;
    private int coste_alquiler;


}
