package ProyectoFinalTienda.TiendaVideojuegos.dtos.requests;

import ProyectoFinalTienda.TiendaVideojuegos.dtos.responses.PersonaResponse;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BloqueoUpdateRequest {
    private int bloqueoId;
    private PersonaResponse persona;
    private String fechaInicio;
    private String fechaFin;
    private String motivo;

}
