package ProyectoFinalTienda.TiendaVideojuegos.dtos.requests;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.*;
import lombok.*;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AlquilerCreateOrReplaceRequest {

    @NotBlank(message = "El DNI de la persona es obligatorio")
    @Pattern(
            regexp = "\\d{7,8}",
            message = "El DNI debe contener 7 u 8 dígitos"
    )
    private String personaDni;

    @NotNull(message = "La fecha en la que se realizó el alquiler es obligatoria")
    private LocalDate fechaInicio;

    @NotNull(message = "La fecha en la que se debe devolver el juego es obligatoria")
    @Future(message = "La fecha de devolución debe estar en el futuro")
    private LocalDate fechaFin;

    @NotEmpty(message = "Debe haber al menos un juego en el alquiler")
    private List<DetalleAlquilerRequest> detalles;

}
