package ProyectoFinalTienda.TiendaVideojuegos.dtos.responses;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BloqueoResponse {

    private int blacklist_id;

    private PersonaResponse personaResponse;

    private String fecha_inicio;

    private String fecha_fin;

    private String motivo;

}
