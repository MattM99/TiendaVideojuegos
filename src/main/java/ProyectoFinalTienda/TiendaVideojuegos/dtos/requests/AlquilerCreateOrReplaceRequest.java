package ProyectoFinalTienda.TiendaVideojuegos.dtos.requests;

import ProyectoFinalTienda.TiendaVideojuegos.model.entities.AlquilerEntity;
import ProyectoFinalTienda.TiendaVideojuegos.model.entities.PersonaEntity;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AlquilerCreateOrReplaceRequest {

    @NotNull(message = "El ID de la persona es obligatorio")
    private Integer personaID;

    @NotNull(message = "La fecha en la que se realizó el alquiler es obligatoria")
    private LocalDate fecha_retiro;

    @NotNull(message = "La fecha en la que se debe devolver el juego es obligatoria")
    private LocalDate fecha_devolucion;

    @NotNull(message = "El juego a alquilar es obligatorio")
    private int idJuego;

}
