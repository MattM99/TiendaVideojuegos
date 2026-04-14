package ProyectoFinalTienda.TiendaVideojuegos.dtos.responses;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BloqueoResponse {

    private int bloqueoId;

    private PersonaResponse personaResponse;

    private String fechaInicio;

    private String fechaFin;

    private String motivo;

}
