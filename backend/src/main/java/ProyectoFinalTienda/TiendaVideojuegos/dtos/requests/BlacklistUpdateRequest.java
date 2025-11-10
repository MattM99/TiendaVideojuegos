package ProyectoFinalTienda.TiendaVideojuegos.dtos.requests;

import ProyectoFinalTienda.TiendaVideojuegos.dtos.responses.PersonaResponse;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BlacklistUpdateRequest {
    private int blacklistId;
    private PersonaResponse persona;
    private String fecha_inicio;
    private String fecha_fin;
    private String motivo;

}
