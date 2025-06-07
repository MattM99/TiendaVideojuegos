package ProyectoFinalTienda.TiendaVideojuegos.dtos.responses;

import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AlquilerResponse {

    private int alquiler_id;

    private PersonaResponse personaResponse;

    private LocalDate fecha_retiro;

    private LocalDate fecha_devolucion;
}
