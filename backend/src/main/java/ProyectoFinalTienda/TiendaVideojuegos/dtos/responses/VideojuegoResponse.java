package ProyectoFinalTienda.TiendaVideojuegos.dtos.responses;

import ProyectoFinalTienda.TiendaVideojuegos.model.enums.Generos;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
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

    @JsonIgnore
    private boolean multijugador; //No se va a mostrar en la respuesta JSON, pero se puede usar internamente

    @JsonProperty("multijugador")
    public String getMultijugadorTexto() {
        return multijugador ? "Sí" : "No";
    }

}

