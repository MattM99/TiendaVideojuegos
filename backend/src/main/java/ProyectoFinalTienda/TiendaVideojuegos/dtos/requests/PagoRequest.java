package ProyectoFinalTienda.TiendaVideojuegos.dtos.requests;
import jakarta.validation.constraints.NotNull;

public class PagoRequest {

    @NotNull(message = "El ID del alquiler es obligatorio")
    private int alquilerId;

}
