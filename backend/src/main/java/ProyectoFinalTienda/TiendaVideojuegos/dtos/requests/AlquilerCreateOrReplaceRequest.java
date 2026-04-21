package ProyectoFinalTienda.TiendaVideojuegos.dtos.requests;

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
    private Integer personaId;

    @NotNull(message = "La fecha en la que se realizó el alquiler es obligatoria")
    private LocalDate fechaInicio;

    @NotNull(message = "La fecha en la que se debe devolver el juego es obligatoria")
    private LocalDate fechaFin;

    @NotNull(message = "El juego a alquilar es obligatorio")
    private int idJuego;

}
