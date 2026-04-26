package ProyectoFinalTienda.TiendaVideojuegos.dtos.requests;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DetalleAlquilerRequest {

    @NotNull(message = "El ID del alquiler es obligatorio")
    private int alquilerId;

    @NotNull(message = "El ID del juego es obligatorio")
    private int inventarioItemId;

    @NotNull(message = "La cantidad es obligatoria")
    @Positive(message = "La cantidad debe ser mayor a 0")
    private int cantidad;




}
