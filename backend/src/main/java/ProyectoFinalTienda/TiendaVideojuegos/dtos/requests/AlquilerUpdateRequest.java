package ProyectoFinalTienda.TiendaVideojuegos.dtos.requests;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AlquilerUpdateRequest {

    @NotNull(message = "La fecha de fin es obligatoria")
    @Future(message = "La fecha de fin debe estar en el futuro")
    private LocalDate fechaFin;
}
