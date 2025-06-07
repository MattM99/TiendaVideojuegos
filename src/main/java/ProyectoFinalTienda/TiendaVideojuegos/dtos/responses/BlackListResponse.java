package ProyectoFinalTienda.TiendaVideojuegos.dtos.responses;

import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BlackListResponse {

    private int blacklist_id;

    private PersonaResponse personaResponse;

    private LocalDate fecha_inicio;

    private LocalDate fecha_fin;

    private String motivo;

}
