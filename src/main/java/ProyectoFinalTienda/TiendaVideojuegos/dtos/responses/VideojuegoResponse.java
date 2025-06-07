package ProyectoFinalTienda.TiendaVideojuegos.dtos.responses;

import ProyectoFinalTienda.TiendaVideojuegos.model.enums.Generos;
import lombok.*;

import java.time.Year;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VideojuegoResponse {

    private int videojuegoID;
    private String titulo;
    private String desarrollador;
    private Generos genero;
    private Year lanzamiento;
    private String descripcion;
    private boolean multijugador;

}

