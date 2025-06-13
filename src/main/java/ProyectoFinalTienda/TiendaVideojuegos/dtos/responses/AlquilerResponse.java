package ProyectoFinalTienda.TiendaVideojuegos.dtos.responses;

import lombok.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

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

    private DetalleAlquilerResponse detalles;

}
