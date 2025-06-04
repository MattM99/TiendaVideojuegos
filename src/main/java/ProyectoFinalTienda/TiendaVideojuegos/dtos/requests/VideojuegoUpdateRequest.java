package ProyectoFinalTienda.TiendaVideojuegos.dtos.requests;

import ProyectoFinalTienda.TiendaVideojuegos.model.entities.VideojuegoEntity;
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

    public void actualizarEntidad(VideojuegoEntity entity) {
        if (titulo != null) entity.setTitulo(titulo);
        if (desarrollador != null) entity.setDesarrollador(desarrollador);
        if (genero != null) entity.setGenero(genero);
        if (lanzamiento != null) entity.setLanzamiento(lanzamiento);
        if (descripcion != null) entity.setDescripcion(descripcion);
        if (multijugador != null) entity.setMultijugador(multijugador);
    }
}
