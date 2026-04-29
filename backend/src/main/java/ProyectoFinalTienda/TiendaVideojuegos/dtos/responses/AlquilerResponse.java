package ProyectoFinalTienda.TiendaVideojuegos.dtos.responses;

import lombok.*;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AlquilerResponse {

    private int alquilerId;

    private PersonaResponse personaResponse;

    private LocalDate fechaInicio;

    private LocalDate fechaFin;

    private List<DetalleAlquilerResponse> carrito;

}
