package ProyectoFinalTienda.TiendaVideojuegos.dtos.requests;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDate;
import java.util.List;

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

    @NotEmpty(message = "Debe haber al menos un juego en el alquiler")
    private List<DetalleAlquilerRequest> detalles;

}
