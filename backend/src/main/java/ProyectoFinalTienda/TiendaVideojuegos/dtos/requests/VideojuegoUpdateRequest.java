package ProyectoFinalTienda.TiendaVideojuegos.dtos.requests;

import ProyectoFinalTienda.TiendaVideojuegos.model.enums.Generos;
import lombok.*;

import java.time.Year;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VideojuegoUpdateRequest {

    private String titulo;
    private String desarrollador;
    private Generos genero;
    private Year lanzamiento;
    private String descripcion;
    private Boolean multijugador;

}
