package ProyectoFinalTienda.TiendaVideojuegos.dtos.requests;

import ProyectoFinalTienda.TiendaVideojuegos.model.enums.MetodosPago;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class CerrarAlquilerRequest {

    @NotNull
    private LocalDate fechaDevolucion;

    @NotNull
    private MetodosPago metodoPago;

    @DecimalMin("0.0")
    @DecimalMax("1.0")
    private BigDecimal descuento = BigDecimal.ZERO;

    private List<PenalizacionManualRequest> penalizaciones
            = new ArrayList<>();
}
