package ProyectoFinalTienda.TiendaVideojuegos.dtos.requests;

import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReservaRequest {

    @NotNull
    private Integer personaId;

}
