package ProyectoFinalTienda.TiendaVideojuegos.dtos.requests;

import ProyectoFinalTienda.TiendaVideojuegos.model.entities.BloqueoEntity;
import ProyectoFinalTienda.TiendaVideojuegos.model.entities.PersonaEntity;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BloqueoCreateOrReplaceRequest {

    @NotNull(message = "El ID de la persona es obligatorio")
    private Integer personaID;

    @NotNull(message = "La fecha de inicio es obligatoria")
    private LocalDate fecha_inicio;

    private LocalDate fecha_fin; // Puede ser null para indicar bloqueo indefinido

    @NotNull(message = "El motivo es obligatorio")
    private String motivo;

    public BloqueoEntity toEntity(PersonaEntity persona) {
        return BloqueoEntity.builder()
                .persona(persona)
                .fechaInicio(this.fecha_inicio)
                .fechaFin(this.fecha_fin)
                .motivo(this.motivo)
                .build();
    }

}
