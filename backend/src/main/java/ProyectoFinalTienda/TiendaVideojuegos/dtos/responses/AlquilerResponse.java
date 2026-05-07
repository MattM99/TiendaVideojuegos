package ProyectoFinalTienda.TiendaVideojuegos.dtos.responses;

import ProyectoFinalTienda.TiendaVideojuegos.model.enums.EstadoAlquiler;
import lombok.*;

import java.math.BigDecimal;
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

    private LocalDate fechaDevolucion;

    private EstadoAlquiler estadoAlquiler;

    private List<DetalleAlquilerResponse> carrito;

    private List<PenalizacionResponse> penalizaciones;

    private BigDecimal montoDiarioAlquiler;

    private PagoResponse pago;
}
