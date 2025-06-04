package ProyectoFinalTienda.TiendaVideojuegos.dtos.requests;
import ProyectoFinalTienda.TiendaVideojuegos.model.entities.VideojuegoEntity;
import ProyectoFinalTienda.TiendaVideojuegos.model.enums.Generos;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.Year;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VideojuegoCreateRequest {

    @NotBlank(message = "El título no puede estar en blanco")
    private String titulo;

    @NotBlank(message = "El desarrollador no puede estar en blanco")
    private String desarrollador;

    @NotNull(message = "El género es obligatorio")
    private Generos genero;

    @NotNull(message = "El año de lanzamiento es obligatorio")
    private Year lanzamiento;

    @NotBlank(message = "La descripción no puede estar en blanco")
    private String descripcion;

    private boolean multijugador;

    public VideojuegoEntity toEntity() {
        return VideojuegoEntity.builder()
                .titulo(titulo)
                .desarrollador(desarrollador)
                .genero(genero)
                .lanzamiento(lanzamiento)
                .descripcion(descripcion)
                .multijugador(multijugador)
                .build();
    }
}
